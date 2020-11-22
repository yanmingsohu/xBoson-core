////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/Util.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xboson.app.lib.ConfigImpl;
import com.xboson.app.lib.IOTImpl;
import com.xboson.app.lib.ModuleHandleContext;
import com.xboson.app.lib.SysImpl;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.util.MongoDBPool;
import com.xboson.util.SysConfig;
import com.xboson.util.SystemNow;
import com.xboson.util.c0nst.IConstant;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public final class Util implements IotConst {

  static final Log log = LogFactory.create("IOT.runtimes");

  final short node;
  final SystemNow now;

  private ConfigImpl conf;
  private SecureRandom rand;
  private Base64.Encoder b64e;
  private String saveDataPath;


  public Util() {
    this.conf = new ConfigImpl();
    this.node = ClusterManager.me().localNodeIDs();
    this.rand = new SecureRandom();
    this.b64e = Base64.getEncoder();
    this.saveDataPath = SysConfig.me().readConfig().configPath +"/paho-mq";
    now = new SystemNow(2000);
  }


  Document openConf() throws RemoteException {
    Document c = conf.get(CONF_NAME);
    if (c == null) {
      throw new RemoteException("Cannot open config: "+ CONF_NAME);
    }
    return c;
  }


  Document openProduct(String pid)
          throws RemoteException {
    Document pwhere = new Document("_id", pid);
    Document p = openDb(TABLE_PRODUCT).find(pwhere).first();
    if (p == null) {
      throw new RemoteException("Product no exists");
    }
    return p;
  }


  String hasProduct(String scenesid, String productid)
          throws RemoteException {
    String id = id(scenesid, productid);
    MongoDatabase db = checkAuth(scenesid);
    Document pwhere = new Document("_id", id);
    if (db.getCollection(TABLE_PRODUCT).count(pwhere) != 1) {
      throw new RemoteException("Product not exists");
    }
    return id;
  }


  MongoDatabase checkAuth(String scenesid) throws RemoteException  {
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


  Document openAddress(String _id) throws RemoteException {
    Document where = new Document("_id", _id);
    FindIterable<Document> res = openDb(TABLE_ADDRESS).find(where);
    Document addr = res.first();
    if (addr == null) {
      throw new RemoteException("Address not exists");
    }
    return addr;
  }


  Document getUser(String user) throws RemoteException {
    SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
    Document where = new Document("_id", user);
    where.put("owner", sys.getUserIdByOpenId());
    Document r = openDb("user").find(where).first();
    if (r == null) {
      throw new RemoteException("User not exists");
    }
    return r;
  }


  String genPassword(String name, String pass) throws RemoteException {
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


  MqttAsyncClient openMqtt(String clientId, String username, int idx, MqttCallback mc)
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


  String getBrokerURL() throws RemoteException {
    Document conf = openConf();
    int port = (int)(double) conf.getDouble("mqport");
    return "tcp://"+ conf.getString("mqhost") +":"+ port;
  }


  MongoCollection<Document> openDb(String collname) throws RemoteException {
    return openDb().getCollection(collname);
  }


  MongoDatabase openDb() throws RemoteException {
    Document conf = openConf();
    String mongodb = conf.getString("mongodb");
    MongoDBPool.VirtualMongoClient cli = MongoDBPool.me().get(mongodb);
    return cli.getDatabase(conf.getString("dbname"));
  }


  static String id(String scenesid, String productid) {
    return '.'+ scenesid +'.'+  productid;
  }


  static String id(String scenesid, String productid, String devid) {
    return '.'+ scenesid +'.'+  productid +'.'+ devid;
  }


  static String toTopic(String _id) {
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
