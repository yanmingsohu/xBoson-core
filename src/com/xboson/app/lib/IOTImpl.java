////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-20 下午12:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/IOTImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.distributed.MultipleExportOneReference;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.MongoDBPool;
import com.xboson.util.SysConfig;
import com.xboson.util.SystemNow;
import com.xboson.util.c0nst.IConstant;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;


public class IOTImpl extends RuntimeUnitImpl implements IAResource {

  private static final String XBOSON_WORKER = "(xboson-worker)";
  private static final String RPC_NAME = "XB.rpc.IOT.Runtime";
  private static final String CONF_NAME = "iot-manager";
  private static final int MAX_THREAD = 30;
  private static final int CONN_TIMEOUT_SEC = 10;

  public static final int TYPE_DATA  = 1;
  public static final int TYPE_EVENT = 2;
  public static final int TYPE_STATE = 3;
  public static final int TYPE_CMD   = 4;
  public static final int TYPE_SAVE  = 5;

  public static final int QOS_0 = 0;
  public static final int QOS_1 = 1;
  public static final int QOS_2 = 2;

  private static final Log log = LogFactory.create("IOT.runtimes");
  private static final Map<String, Integer> typeInt;
  private static final Class<IWorkThread>[] work_type = new Class[] {
          null, // index 0 is null
          DataTopicProcess.class,
          EventTopicProcess.class,
          StateTopicProcess.class,
          SaveTopicProcess.class,
  };

  static {
    typeInt = initTypeMap();
  }


  public IOTImpl() {
    super(null);
  }


  public static void regTo(RpcFactory rpc) {
    rpc.bindOnce(new StubService(), RPC_NAME);
  }


  @Override
  public String description() {
    return "app.module.iot.functions()";
  }


  public Object open() {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    return new ClusterService();
  }


  private static Map<String, Integer> initTypeMap() {
    Map<String, Integer> map = new HashMap<>();
    for (int i=1; i<work_type.length; ++i) {
      try {
        String name = work_type[i].newInstance().name();
        map.put(name, i);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    return map;
  }


  public static class WorkerInfo {
    public String name;
    // mqtt 主题字符串
    public String topic;
    // 工作节点
    public short node;
    // 线程id, 总是从 0 开始, 来自索引
    public long tid;
    // 处理数据量
    public long count;
    // 最后数据时间
    public long time;
    // 状态描述
    public String stateMsg;
    // 出错数量
    public long error;
    public String qos;
  }


  public interface IWorkThread {

    /**
     * 启动线程
     * @param pid    产品id
     * @param qos    数据质量
     * @param user   mq用户
     * @param script 脚本, 可以空
     * @param index  线程索引, 从0开始, 作为 WorkerInfo.tid
     * @throws RemoteException
     */
    void start(Util util, String pid, int qos, String user, String script, int index)
            throws RemoteException;


    /**
     * 停止线程并等待, 如果线程已经停止则立即返回
     * @throws RemoteException 如果停止失败
     */
    void stop() throws RemoteException;


    /**
     * 如果线程正在运行返回 true
     * 保证已经停止的线程可以安全回收内存
     */
    boolean isRunning();


    /**
     * 返回线程状态
     */
    WorkerInfo info();


    /**
     * 返回 topic 名称
     */
    String name();
  }


  private static abstract class AbsWorker implements IWorkThread {
    WorkerInfo info;
    Util util;
    String pid;
    int qos;
    String user;
    String script;
    boolean running;
    IMqttAsyncClient mq;
    String clientid;


    private AbsWorker() {
      this.info = new WorkerInfo();
    }


    /**
     * 启动线程, 该方法返回意味着一切正常, 否则应该抛出异常
     */
    abstract void start() throws RemoteException, MqttException;


    @Override
    public void start(Util util, String pid, int qos, String user, String script, int idx)
            throws RemoteException
    {
      this.util   = util;
      this.pid    = pid;
      this.qos    = qos;
      this.user   = user;
      this.script = script;

      info.name     = name();
      info.topic    = toTopic(pid) +"/+/"+ name();
      info.node     = util.node;
      info.tid      = idx;
      info.qos      = "QoS"+ qos;
      info.stateMsg = "正在启动";

      // 这个属性不输出到任何地方
      this.clientid = XBOSON_WORKER + name() + pid +'.'+ util.node +'.'+ idx;

      try {
        mq = util.openMqtt(clientid, user, idx);
        start();
        running = true;
        info.stateMsg = "运行中";
      } catch(Exception err) {
        info.error++;
        info.stateMsg = "启动错误 "+ err.getMessage();
        log.error("Start work", err);
        err.printStackTrace();
        stop();
      }
    }


    private IMqttActionListener statusListener() {
      return new IMqttActionListener() {
        public void onSuccess(IMqttToken iMqttToken) {
        }

        public void onFailure(IMqttToken iMqttToken, Throwable t) {
          info.error++;
          info.stateMsg = "异常, 重新连接, " + t.getMessage();
          log.error("On Failure", t);
          try {
            mq.reconnect();
          } catch (MqttException e) {
            info.error++;
            info.stateMsg = "重新连接失败, "+ e.getMessage();

            try {
              stop();
            } catch (RemoteException e1) {
              e1.printStackTrace();
            }
          }
        }
      };
    }


    @Override
    public WorkerInfo info() {
      return info;
    }


    @Override
    public void stop() throws RemoteException {
      if (!running) return;
      try {
        info.stateMsg = "正在停止";
        if (mq.isConnected()) {
          mq.disconnect().waitForCompletion();
        }
        mq.close();
        info.stateMsg = "停止";
        running = false;
      } catch (MqttException e) {
        info.error++;
        throw new RemoteException(e.getMessage());
      }
    }


    @Override
    public boolean isRunning() {
      return running;
    }


    void addCounter() {
      info.count++;
      info.time = util.now.now;
    }
  }


  public static class StateTopicProcess extends AbsWorker implements IMqttMessageListener {

    @Override
    void start() throws RemoteException, MqttException {
      mq.subscribe(info.topic, qos, this);
    }

    @Override
    public String name() {
      return "state";
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
      log.warn("MSG", clientid, topic, mqttMessage);
      addCounter();
    }
  }


  public static class SaveTopicProcess extends AbsWorker {

    @Override
    void start() throws RemoteException {
    }

    @Override
    public String name() {
      return "save";
    }
  }


  public static class EventTopicProcess extends AbsWorker {

    @Override
    void start() throws RemoteException {
    }

    @Override
    public String name() {
      return "event";
    }
  }


  public static class DataTopicProcess extends AbsWorker {

    @Override
    void start() throws RemoteException {
    }

    @Override
    public String name() {
      return "data";
    }
  }


  public interface IRPC extends IXRemote {

    /**
     * 恢复所有处理器线程
     * @param scenesid 场景
     * @param productid 产品
     * @throws RemoteException
     */
    void restore(String scenesid, String productid) throws RemoteException;

    /**
     * 返回所有线程状态
     */
    WorkerInfo[] info(String scenesid, String productid) throws RemoteException;

    /**
     * 停止所有线程
     */
    void stopAll(String scenesid, String productid) throws RemoteException;

    /**
     * 停止指定节点上的指定线程
     */
    void stop(String sid, String pid, String node, String type, int index) throws RemoteException;
  }


  public class ClusterService implements IRPC {
    private MultipleExportOneReference<IRPC> mr;


    private ClusterService() {
      mr = new MultipleExportOneReference<>(RPC_NAME);
    }


    @Override
    public void restore(String sid, String pid) throws RemoteException {
      mr.each((i, node, remote) -> {
        remote.restore(sid, pid);
        return true;
      });
    }


    @Override
    public WorkerInfo[] info(String scenesid, String productid) throws RemoteException {
      final List<WorkerInfo> list = new ArrayList<>();
      mr.each((i, node, remote) -> {
        Collections.addAll(list, remote.info(scenesid, productid));
        return true;
      });
      return list.toArray(new WorkerInfo[0]);
    }


    @Override
    public void stopAll(String scenesid, String productid) throws RemoteException {
      mr.each((i, node, remote) -> {
        remote.stopAll(scenesid, productid);
        return true;
      });
    }


    @Override
    public void stop(String sid, String pid, String node, String type, int index)
            throws RemoteException
    {
      IRPC remote = mr.get(node);
      if (remote == null) {
        throw new RemoteException("Get node fail");
      }
      remote.stop(sid, pid, node, type, index);
    }
  }


  private static class Util {
    private ConfigImpl conf;
    private short node;
    private SecureRandom rand;
    private Base64.Encoder b64e;
    private String saveDataPath;
    private SystemNow now;


    private Util() {
      this.conf = new ConfigImpl();
      this.node = ClusterManager.me().localNodeIDs();
      this.rand = new SecureRandom();
      this.b64e = Base64.getEncoder();
      this.saveDataPath = SysConfig.me().readConfig().configPath +"/paho-mq";
      now = new SystemNow(2000);
    }


    private Document openConf() throws RemoteException {
      Document c = conf.get(CONF_NAME);
      if (c == null) {
        throw new RemoteException("Cannot open config: "+ CONF_NAME);
      }
      return c;
    }


    private Document openProduct(String pid)
            throws RemoteException {
      Document pwhere = new Document("_id", pid);
      Document p = openDb("product").find(pwhere).first();
      if (p == null) {
        throw new RemoteException("Product no exists");
      }
      return p;
    }


    private String hasProduct(String scenesid, String productid)
            throws RemoteException {
      String id = id(scenesid, productid);
      MongoDatabase db = checkAuth(scenesid);
      Document pwhere = new Document("_id", id);
      if (db.getCollection("product").count(pwhere) != 1) {
        throw new RemoteException("Product not exists");
      }
      return id;
    }


    private MongoDatabase checkAuth(String scenesid) throws RemoteException  {
      SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");

      List or = new ArrayList<>();
      or.add(new Document("owner", sys.getUserIdByOpenId()));
      or.add(new Document("share", sys.getUserIdByOpenId()));
      Document swhere = new Document("$or", or);
      MongoDatabase db = openDb();

      if (db.getCollection("scenes").count(swhere) < 1) {
        throw new RemoteException("无权操作场景 "+ scenesid);
      }
      return db;
    }


    private Document openAddress(String _id) throws RemoteException {
      Document where = new Document("_id", _id);
      FindIterable<Document> res = openDb("address").find(where);
      Document addr = res.first();
      if (addr == null) {
        throw new RemoteException("Address not exists");
      }
      return addr;
    }


    private Document getUser(String user) throws RemoteException {
      SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
      Document where = new Document("_id", user);
      where.put("owner", sys.getUserIdByOpenId());
      Document r = openDb("user").find(where).first();
      if (r == null) {
        throw new RemoteException("User not exists");
      }
      return r;
    }


    private String genPassword(String name, String pass) throws RemoteException {
      try {
        byte[] b = new byte[16 + 16];
        rand.nextBytes(b);
        MessageDigest md = MessageDigest.getInstance("md5");
        md.update(b, 0, 16);
        md.update(name.getBytes(IConstant.CHARSET));
        md.update(pass.getBytes(IConstant.CHARSET));
        md.digest(b, 16, 16);
        return b64e.encodeToString(b);

      } catch (Exception e) {
        throw new RemoteException(e.getMessage());
      }
    }


    private MqttAsyncClient openMqtt(String clientId, String username, int idx)
            throws RemoteException, MqttException
    {
      Document conf = openConf();
      int port = (int)(double) conf.getDouble("mqport");
      String broker = "tcp://"+ conf.getString("mqhost") +":"+ port;

      Document user = getUser(username);
      String pass = genPassword(username, user.getString("password"));

      MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(saveDataPath);
      MqttConnectOptions opt = new MqttConnectOptions();
      MqttAsyncClient cli = new MqttAsyncClient(broker, clientId, persistence);

      opt.setUserName(username);
      opt.setPassword(pass.toCharArray());
      opt.setConnectionTimeout(CONN_TIMEOUT_SEC);

      // 只有 0 节点上的 0 线程持久保留数据, 其他线程在断开后清除数据
      if (node == 0 && idx == 0) {
        opt.setCleanSession(false);
      } else {
        opt.setCleanSession(true);
      }

      log.debug("Connect to", broker, "Wait...");
      cli.connect(opt).waitForCompletion();
      log.debug("Connected to", broker, "client ID:", clientId, username);
      return cli;
    }


    private MongoCollection<Document> openDb(String collname) throws RemoteException {
      return openDb().getCollection(collname);
    }


    private MongoDatabase openDb() throws RemoteException {
      Document conf = openConf();
      String mongodb = conf.getString("mongodb");
      MongoDBPool.VirtualMongoClient cli = MongoDBPool.me().get(mongodb);
      return cli.getDatabase(conf.getString("dbname"));
    }
  }


  public static class StubService implements IRPC {
    private Map<String, Procuct> prods;
    private Util util;


    StubService() {
      this.prods = new HashMap<>();
      this.util = new Util();
    }


    public synchronized void restore(String scenesid, String productid)
            throws RemoteException
    {
      Procuct p = getProcuct(scenesid, productid, false);
      if (p == null) {
        String pid = id(scenesid, productid);
        util.hasProduct(scenesid, productid);
        p = new Procuct(pid, util);
        prods.put(pid, p);
      }

      p.restore();
    }


    @Override
    public synchronized WorkerInfo[] info(String sid, String pid) throws RemoteException {
      ArrayList<WorkerInfo> list = new ArrayList<>();
      Procuct p = getProcuct(sid, pid, false);
      if (p != null) {
        p.info(list);
      }
      return list.toArray(new WorkerInfo[0]);
    }


    @Override
    public synchronized void stopAll(String scenesid, String productid)
            throws RemoteException
    {
      Procuct p = getProcuct(scenesid, productid, true);
      p.stopAll();
    }


    @Override
    public synchronized void stop(String sid, String pid, String _node,
                                  String type, int index)
                                  throws RemoteException
    {
      Integer itype = typeInt.get(type);
      if (itype == null) {
        throw new RemoteException("Invaild type");
      }

      Procuct p = getProcuct(sid, pid, true);
      IWorkThread w = p.find(itype, index);
      if (w == null) {
        throw new RemoteException("Could not find thread.");
      }
      if (! w.isRunning()) {
        throw new RemoteException("The Thread Not Running.");
      }

      w.stop();
    }


    private Procuct getProcuct(String sid, String pid, boolean checknull)
            throws RemoteException
    {
      util.checkAuth(sid);
      String id = id(sid, pid);
      Procuct p = prods.get(id);
      if (checknull && p == null) {
        throw new RemoteException("Product not exists "+ id);
      }
      return p;
    }
  }


  public static class Procuct {

    private Map<Integer, Deque<IWorkThread>> workers;
    private Util util;
    private String id;


    private Procuct(String id, Util util) {
      this.workers = new HashMap<>();
      this.util = util;
      this.id = id;
    }


    private void restore() throws RemoteException {
      clearDead();
      Document addr = util.openAddress(id);
      restore(TYPE_DATA,  (Document) addr.get("data"));
      restore(TYPE_EVENT, (Document) addr.get("event"));
      restore(TYPE_STATE, (Document) addr.get("state"));
      restore(TYPE_SAVE,  (Document) addr.get("save"));
    }


    private void stopAll() throws RemoteException {
      stop(TYPE_DATA, 0);
      stop(TYPE_EVENT, 0);
      stop(TYPE_STATE, 0);
      stop(TYPE_SAVE, 0);
    }


    private void restore(int type, Document topicConf) throws RemoteException {
      int count = 0;
      if (topicConf != null) {
        count = targetCount( topicConf.getLong("count") );
      }

      if (count(type) < count) {
        String user = topicConf.getString("user");
        String script = topicConf.getString("script");
        long qos = topicConf.getLong("qos");

        start(type, count, (int)qos, user, script);
      } else {
        stop(type, count);
      }
    }


    /**
     * 当总任务为 c 的时候, 计算当前节点分配任务量
     */
    private int targetCount(Long c) {
      if (c == null) return 0;

      ClusterManager cm = ClusterManager.me();
      String self = cm.localNodeID();
      Object[] nodes = cm.list().toArray();
      if (nodes.length == 1) return c.intValue();

      int myindex = 0;
      for (int i=0; i<nodes.length; ++i) {
        if (self.equals(nodes[i])) {
          myindex = i;
          break;
        }
      }

      final int nodeCount = nodes.length;
      int[] cluster = new int[nodeCount];
      int node = 0;

      while (c > 0) {
        cluster[node % nodeCount]++;
        --c;
        ++node;
      }
      return cluster[myindex];
    }


    /**
     * 启动 type 类型的任务直到 count 个
     */
    private void start(int type, int count, int qos, String user, String script)
            throws RemoteException
    {
      for (int i=0; i<count; ++i) {
        try {
          IWorkThread worker = find(type, i);
          if (worker != null && worker.isRunning()) {
            continue;
          } else {
            worker = newWorker(type);
          }

          worker.start(util, id, qos, user, script, i);
          pushWork(type, worker);
        } catch (Exception e) {
          throw new RemoteException("Start work "+ i +" fail: "+ e.getMessage());
        }
      }
    }


    /**
     * 终止任务, 直到线程数量达到 count
     */
    private void stop(int type, int count) throws RemoteException {
      Deque<IWorkThread> stack = workers.get(type);
      if (stack == null) return;

      while (stack.size() > count) {
        IWorkThread work = stack.getLast();
        work.stop();
        stack.removeLast();
      }
    }


    /**
     * 寻找指定线程,
     * @param type 类型
     * @param index 索引
     * @return 找不到返回 null
     */
    private IWorkThread find(int type, int index) {
      Deque<IWorkThread> stack = workers.get(type);
      if (stack == null) return null;

      for (IWorkThread work : stack) {
        if (work.info().tid == index) {
          return work;
        }
      }
      return null;
    }


    /**
     * 返回所有线程的状态
     */
    private void info(List<WorkerInfo> list) {
      for (int type : workers.keySet()) {
        Deque<IWorkThread> stack = workers.get(type);
        for (IWorkThread w : stack) {
          list.add(w.info());
        }
      }
    }


    private IWorkThread newWorker(int type) throws RemoteException {
      if ((type < 1) || (type >= work_type.length)) {
        throw new RemoteException("Work type invalid");
      }
      try {
        Class<IWorkThread> tc = work_type[type];
        return tc.newInstance();
      } catch (Exception e) {
        throw new RemoteException("Cannot create new work "+ e.getMessage());
      }
    }


    private void pushWork(int type, IWorkThread worker) {
      Deque<IWorkThread> stack = workers.get(type);
      if (stack == null) {
        stack = new LinkedBlockingDeque<>(MAX_THREAD);
        workers.put(type, stack);
      }
      stack.addLast(worker);
    }


    private int count(int type) {
      if (! workers.containsKey(type)) {
        return 0;
      }
      return workers.get(type).size();
    }


    /**
     * 清除死线程
     */
    private void clearDead() {
      for (int type : workers.keySet()) {
        Deque<IWorkThread> stack = workers.get(type);

        for (int i= stack.size()-1; i>=0; --i) {
          IWorkThread w = stack.pollFirst();
          if (w != null && w.isRunning()) {
            stack.addLast(w);
          }
        }
      }
    }
  }


  private static String id(String scenesid, String productid) {
    return "."+ scenesid +"."+  productid;
  }


  private static String toTopic(String _id) {
    StringBuilder r = new StringBuilder();
    for (int i=0; i<_id.length(); ++i) {
      char c = _id.charAt(i);
      if (c == '.') {
        r.append('/');
      } else {
        r.append(c);
      }
    }
    return r.toString();
  }
}
