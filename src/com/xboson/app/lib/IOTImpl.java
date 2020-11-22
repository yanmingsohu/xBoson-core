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
import com.squareup.moshi.JsonAdapter;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.XBosonException;
import com.xboson.distributed.MultipleExportOneReference;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.script.IVisitByScript;
import com.xboson.util.*;
import com.xboson.util.c0nst.IConstant;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;


public class IOTImpl extends RuntimeUnitImpl implements IAResource {

  interface IotConst {
    String XBOSON_WORKER = "(xboson-worker)";
    String RPC_NAME      = "XB.rpc.IOT.Runtime";
    String CONF_NAME     = "iot-manager";
    String TABLE_DEVICE  = "device";
    String TABLE_PRODUCT = "product";
    String TABLE_SCENES  = "scenes";
    String TABLE_ADDRESS = "address";
    String TABLE_EVENT   = "event_his";

    int EVENT_CODE_MESSAGE_FAIL = 2001;
    int EVENT_LEVEL_MESSAGE_FAIL = 3;
    int MAX_THREAD = 30;
    int CONN_TIMEOUT_SEC = 10;

    int TYPE_DATA  = 1;
    int TYPE_EVENT = 2;
    int TYPE_STATE = 3;
    int TYPE_CMD   = 4;
    int TYPE_SAVE  = 5;

    int QOS_0 = 0;
    int QOS_1 = 1;
    int QOS_2 = 2;
  }

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
    rpc.bindOnce(new StubService(), IotConst.RPC_NAME);
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


  public static class WorkerInfo implements IVisitByScript {
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


  public interface IWorkThread extends IotConst {

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


  /**
   * event_his 表数据结构
   */
  public static class EventDoc implements Serializable {
    public Integer code;
    public String msg;
    public Long cd;
    public Integer level;
    public Object data;

    public EventDoc() {}

    public EventDoc(int code, String msg, int level, long cd) {
      this.code  = code;
      this.msg   = msg;
      this.level = level;
      this.cd    = cd;
    }
  }


  /**
   * 事件处理器基类, 子类必须是 public 否则 rpc 报错
   */
  private static abstract class AbsWorker implements IWorkThread, MqttCallbackExtended {

    final Object placeholder;
    final Map<String, Object> deviceCache;
    final WorkerInfo info;
    final String name;
    MongoCollection<Document> deviceTable;
    MongoCollection<Document> productTable;
    MongoCollection<Document> eventTable;
    IMqttAsyncClient mq;
    Util util;
    String pid;
    String user;
    String script;
    String clientid;
    // 运行中不表示连接正常, 不表示没有错误
    boolean running;
    int reconnectCnt;
    int qos;


    private AbsWorker() {
      this.info = new WorkerInfo();
      this.deviceCache = Collections.synchronizedMap(new WeakHashMap<>());
      this.placeholder = new Object();
      this.name = name();
      info.time = -1;
    }


    /**
     * 在订阅主题前进行一些初始化, 该方法返回意味着一切正常, 否则应该抛出异常
     */
    abstract void beforeSubscribe(boolean reconnect) throws RemoteException, MqttException;


    /**
     * 重写该方法以接收数据, 该函数成功返回数据计数器+1, 抛出异常则错误计数器+1
     */
    abstract void onMessage(String topic, MqttMessage msg) throws Exception;


    @Override
    public void start(Util util, String pid, int qos, String user, String script, int idx)
            throws RemoteException
    {
      this.util   = util;
      this.pid    = pid;
      this.qos    = qos;
      this.user   = user;
      this.script = script;
      // 这个属性不输出到任何地方
      this.clientid = XBOSON_WORKER + name + pid +'.'+ util.node +'.'+ idx;

      info.name     = name;
      info.topic    = toTopic(pid) +"/+/"+ name;
      info.node     = util.node;
      info.tid      = idx;
      info.qos      = "QoS"+ qos;
      info.stateMsg = "正在启动";

      try {
        deviceTable  = util.openDb(TABLE_DEVICE);
        productTable = util.openDb(TABLE_PRODUCT);
        eventTable   = util.openDb(TABLE_EVENT);
        mq = util.openMqtt(clientid, user, idx, this);
        running = true;
      } catch(Exception err) {
        pushError("启动错误", err, false);
        stop();
      }
    }


    @Override
    public void connectionLost(Throwable t) {
      pushError("连接异常", t, false);
      try {
        Tool.sleep(2000);
        info.stateMsg = "正在重新连接";
        mq.reconnect();
      } catch (MqttException e) {
        pushError("重新连接失败", e, false);

        try {
          stop();
        } catch (RemoteException e1) {
          e1.printStackTrace();
        }
      }
    }


    @Override
    public void connectComplete(boolean reconnect, java.lang.String serverURI) {
      try {
        beforeSubscribe(reconnect);
        mq.subscribe(info.topic, qos);

        if (reconnect) {
          ++reconnectCnt;
          info.stateMsg = "运行中(恢复"+ reconnectCnt +")";
        } else {
          info.stateMsg = "运行中";
        }
      } catch (Exception e) {
        pushError("订阅错误", e, false);
      }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
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
        mq = null;
        running = false;
      } catch (MqttException e) {
        pushError("停止失败", e, true);
      }
    }


    private void pushError(String why, Throwable e, boolean _throw) {
      info.error++;
      info.stateMsg = why +','+ e.getMessage();
      log.error(this.getClass(), why, e);
      if (e instanceof NullPointerException) {
        e.printStackTrace();
      }
      if (_throw) {
        throw new XBosonException(e);
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


    @Override
    public final void messageArrived(String topic, MqttMessage msg) throws Exception {
      log.debug("msg", name, info.node, info.tid, topic, msg);
      try {
        onMessage(topic, msg);
        addCounter();
      } catch(Exception e) {
        String name = "处理消息时异常";
        pushError(name, e, false);

        EventDoc event = new EventDoc(EVENT_CODE_MESSAGE_FAIL,
                name, EVENT_LEVEL_MESSAGE_FAIL, util.now.now);
        Map<String, Object> data = new HashMap<>();
        event.data = data;
        data.put("payload", Hex.upperHex(msg.getPayload()));
        data.put("message", e.getMessage());
        data.put("stack",   Tool.miniStack(e, 3));
        TopicInf inf = new TopicInf(topic);
        pushEvent(inf, event);
      }
    }


    /**
     * 创建不存在的设备, 创建了返回 true, 设备已经存在返回 false
     * state 可以为 null
     */
    boolean createNoExistsDevice(TopicInf inf, String state) throws RemoteException {
      String devid = inf.genDeviceID();
      if (deviceCache.containsKey(devid))
        return false;

      Document devdoc = new Document("_id", devid);
      if (deviceTable.count(devdoc) > 0) {
        deviceCache.put(devid, placeholder);
        return false;
      }

      Document meta = new Document();
      Document product = productTable
              .find(new Document("_id", inf.genProductID())).first();

      for (Object odoc : product.get("meta", List.class)) {
        Document mdoc = (Document) odoc;
        meta.put(mdoc.getString("name"), mdoc.get("defval"));
      }

      if (state == null) {
        state = "自动创建";
      }

      Date now = new Date();
      devdoc.put("devid",   inf.device);
      devdoc.put("product", inf.product);
      devdoc.put("scenes",  inf.scenes);
      devdoc.put("state",   state);
      devdoc.put("dc",      0);
      devdoc.put("dd",      0);
      devdoc.put("cd",      now);
      devdoc.put("md",      now);
      devdoc.put("meta",    meta);

      deviceTable.insertOne(devdoc);
      return true;
    }


    /**
     * 推送消息到 mongo 持久化保存
     */
    void pushEvent(TopicInf inf, EventDoc event) throws RemoteException {
      if (event == null) {
        throw new XBosonException("Message not JSON format");
      }
      if (event.msg == null) {
        throw new XBosonException.NullParamException("msg");
      }
      if (event.cd == null) {
        event.cd = util.now.now;
      }
      if (event.code == null) {
        event.code = 0;
      }
      if (event.level == null) {
        event.level = 1;
      }

      Document edoc = new Document();
      edoc.put("code",    event.code);
      edoc.put("msg",     event.msg);
      edoc.put("level",   event.level);
      edoc.put("devid",   inf.genDeviceID());
      edoc.put("product", inf.product);
      edoc.put("scenes",  inf.scenes);
      edoc.put("cd",      new Date(event.cd));
      edoc.put("repwho",  null);
      edoc.put("repmsg",  null);
      edoc.put("reptime", null);
      edoc.put("data",    event.data);

      eventTable.insertOne(edoc);
    }
  }


  public static class StateTopicProcess extends AbsWorker {

    @Override
    void beforeSubscribe(boolean reconnect) throws RemoteException, MqttException {
    }

    @Override
    public String name() {
      return "state";
    }

    @Override
    void onMessage(String topic, MqttMessage msg) throws Exception {
      TopicInf inf = new TopicInf(topic);
      String state = msg.toString();
      if (! createNoExistsDevice(inf, state)) {
        deviceTable.updateOne(new Document("_id", inf.genDeviceID()),
                new Document("$set", new Document("state", state)));
      }
      addCounter();
    }
  }


  public static class EventTopicProcess extends AbsWorker {
    private JsonAdapter<EventDoc> jadapter;

    @Override
    void beforeSubscribe(boolean reconnect) throws RemoteException {
      if (!reconnect) {
        jadapter = Tool.getAdapter(EventDoc.class);
      }
    }

    @Override
    public String name() {
      return "event";
    }

    @Override
    public void onMessage(String topic, MqttMessage msg) throws Exception {
      TopicInf inf = new TopicInf(topic);
      createNoExistsDevice(inf, null);

      EventDoc event = jadapter.fromJson(msg.toString());
      pushEvent(inf, event);
    }
  }


  public static class SaveTopicProcess extends AbsWorker {

    @Override
    void beforeSubscribe(boolean reconnect) throws RemoteException {
    }

    @Override
    public String name() {
      return "save";
    }

    @Override
    public void onMessage(String topic, MqttMessage msg) throws Exception {
    }
  }


  public static class DataTopicProcess extends AbsWorker {

    @Override
    void beforeSubscribe(boolean reconnect) throws RemoteException {
    }

    @Override
    public String name() {
      return "data";
    }

    @Override
    public void onMessage(String topic, MqttMessage msg) throws Exception {
    }
  }


  public interface IRPC extends IXRemote, IotConst {

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


  private static class Util implements IotConst {
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
      Document p = openDb(TABLE_PRODUCT).find(pwhere).first();
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
      if (db.getCollection(TABLE_PRODUCT).count(pwhere) != 1) {
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

      if (db.getCollection(TABLE_SCENES).count(swhere) < 1) {
        throw new RemoteException("无权操作场景 "+ scenesid);
      }
      return db;
    }


    private Document openAddress(String _id) throws RemoteException {
      Document where = new Document("_id", _id);
      FindIterable<Document> res = openDb(TABLE_ADDRESS).find(where);
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


    private MqttAsyncClient openMqtt(String clientId, String username, int idx,
                                     MqttCallback mc)
            throws RemoteException, MqttException
    {
      String broker = getBrokerURL();
      Document user = getUser(username);
      String pass = genPassword(username, user.getString("password"));

      MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(saveDataPath);
      MqttConnectOptions opt = new MqttConnectOptions();
      MqttAsyncClient cli = new MqttAsyncClient(broker, clientId, persistence);
      cli.setCallback(mc);

      opt.setUserName(username);
      opt.setPassword(pass.toCharArray());
      opt.setConnectionTimeout(CONN_TIMEOUT_SEC);
      opt.setAutomaticReconnect(true);

      // 只有 0 节点上的 0 线程持久保留数据, 其他线程在断开后清除数据
      if (node == 0 && idx == 0) {
        opt.setCleanSession(false);
      } else {
        opt.setCleanSession(true);
      }

      cli.connect(opt);
      log.debug("Connected to", broker, "client ID:", clientId, username);
      return cli;
    }


    private String getBrokerURL() throws RemoteException {
      Document conf = openConf();
      int port = (int)(double) conf.getDouble("mqport");
      return "tcp://"+ conf.getString("mqhost") +":"+ port;
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


  private static class Procuct implements IotConst {

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


  public static class TopicInf {
    public String scenes;
    public String product;
    public String device;

    public TopicInf(String topic) {
      if (topic.charAt(0) != '/') {
        throw new XBosonException.BadParameter(
                "Topic name", "not begin '/'");
      }
      int st = 0;
      int a = 1;

      for (int i=a; i<topic.length(); ++i) {
        char c = topic.charAt(i);
        if (c == '/') {
          if (a == i) {
            throw new XBosonException.BadParameter(
                    "Topic name", "no char in '/'");
          }
          switch (st) {
            case 0:
              scenes = topic.substring(a, i);
              break;
            case 1:
              product = topic.substring(a, i);
              break;
            case 2:
              device = topic.substring(a, i);
              return;
          }
          a = i+1;
          ++st;
        }
      }
      if (st == 2) {
        device = topic.substring(a);
      } else {
        throw new XBosonException.BadParameter(
                "Topic name", "bad ending");
      }
    }

    public String genDeviceID() {
      return id(scenes, product, device);
    }

    public String genProductID() {
      return id(scenes, product);
    }

    public String toString() {
      return "scenes="+ scenes +", product="+ product +", device="+ device;
    }
  }


  private static String id(String scenesid, String productid) {
    return '.'+ scenesid +'.'+  productid;
  }


  private static String id(String scenesid, String productid, String devid) {
    return '.'+ scenesid +'.'+  productid +'.'+ devid;
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
