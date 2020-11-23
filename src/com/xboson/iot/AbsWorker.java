////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:09
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/AbsWorker.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.mongodb.client.MongoCollection;
import com.xboson.app.lib.IOTImpl;
import com.xboson.been.XBosonException;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.*;

import java.rmi.RemoteException;
import java.util.*;


/**
 * 事件处理器基类, 子类必须是 public 否则 rpc 报错
 */
public abstract class AbsWorker implements IWorkThread, MqttCallbackExtended {

  protected final Object placeholder;
  protected final Map<String, Object> deviceCache;
  protected final WorkerInfo info;
  protected final String name;

  protected MongoCollection<Document> deviceTable;
  protected MongoCollection<Document> productTable;
  protected MongoCollection<Document> eventTable;

  protected Util util;
  protected String script;
  protected IMqttAsyncClient mq;
  protected int qos;

  private final CpuUsage cu;
  private String pid;
  private String user;
  private String clientid;
  // 运行中不表示连接正常, 不表示没有错误
  private boolean running;
  private int reconnectCnt;


  AbsWorker() {
    this.info = new WorkerInfo();
    this.deviceCache = Collections.synchronizedMap(new WeakHashMap<>());
    this.placeholder = new Object();
    this.name = name();
    this.cu = new CpuUsage(30);
    info.time = -1;
  }


  /**
   * 在订阅主题前进行一些初始化, 该方法返回意味着一切正常, 否则应该抛出异常
   */
  abstract void beforeSubscribe(boolean reconnect)
          throws RemoteException, MqttException;


  /**
   * 重写该方法以接收数据, 该函数成功返回数据计数器+1, 抛出异常则错误计数器+1
   */
  abstract void onMessage(String topic, MqttMessage msg) throws Exception;


  @Override
  public final void start(Util util, String pid, int qos, String user,
                          String script, int idx) throws RemoteException
  {
    this.util   = util;
    this.pid    = pid;
    this.qos    = qos;
    this.user   = user;
    this.script = script;
    // 这个属性不输出到任何地方
    this.clientid = XBOSON_WORKER + name + pid +'.'+ util.node +'.'+ idx;

    info.name     = name;
    info.topic    = Util.toTopic(pid) +"/+/"+ name;
    info.node     = util.node;
    info.tid      = idx;
    info.qos      = "QoS"+ qos;
    info.stateMsg = "正在启动";

    try {
      openTables();
      mq = util.openMqtt(clientid, user, idx, this);
      running = true;
    } catch(Exception err) {
      pushError("启动错误", err, false);
      stop();
    }
  }


  private void openTables() throws RemoteException {
    deviceTable  = util.openDb(TABLE_DEVICE);
    productTable = util.openDb(TABLE_PRODUCT);
    eventTable   = util.openDb(TABLE_EVENT);
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
  public void connectComplete(boolean reconnect, String serverURI) {
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
    cu.reset();
    cu.waitTest();
    info.cpu = cu.cpu();
    return info;
  }


  @Override
  public final void stop() throws RemoteException {
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
    Util.log.error(this.getClass(), why, e);
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
    cu.begin();
    Util.log.debug("msg", name, info.node, info.tid, topic, msg);
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
      data.put("stack",   Tool.xbosonStack(e));
      TopicInf inf = new TopicInf(topic);
      pushEvent(inf, event);
    } finally {
      cu.end();
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
