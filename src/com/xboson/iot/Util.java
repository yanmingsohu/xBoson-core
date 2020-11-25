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
import com.xboson.app.ApiEncryption;
import com.xboson.app.lib.ConfigImpl;
import com.xboson.been.Module;
import com.xboson.been.XBosonException;
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

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;


public final class Util implements IotConst {

  static final Log log = LogFactory.create("IOT.runtimes");
  static final ApiEncryption secr = new ApiEncryption(z);
  static final Pattern idTest = Pattern.compile(ID_TEST_P);

  final short node;
  final SystemNow now;

  private ConfigImpl conf;
  private SecureRandom rand;
  private Base64.Encoder b64e;
  private String saveDataPath;
  private ScriptEnv scriptEnv;


  Util() {
    this.conf = new ConfigImpl();
    this.node = ClusterManager.me().localNodeIDs();
    this.rand = new SecureRandom();
    this.b64e = Base64.getEncoder();
    this.saveDataPath = SysConfig.me().readConfig().configPath + MQ_WORK_PATH;
    this.scriptEnv = new ScriptEnv(this);
    now = new SystemNow(1000);
  }


  /**
   * 从脚本表中选中并启动一个脚本
   * @param path 脚本路径, 即脚本id
   * @return 导出模块, (可能缓存)
   */
  Module run(String path) {
    return scriptEnv.run(path);
  }


  /**
   * 通知脚本被修改, 更新缓存
   * @param id 脚本id
   */
  void changed(String id) {
    scriptEnv.changed(id);
  }


  /**
   * 测试 id 有效性, 非法格式抛出异常, 只能是字母和数字
   * @param id 不能是组合id
   */
  public static void testId(String id) {
    if (! idTest.matcher(id).matches()) {
      throw new XBosonException("ID format fail "+ id);
    }
  }


  /**
   * 打开配置文件, 必须在 http 服务上下文中调用
   */
  Document openConf() throws RemoteException {
    Document c = conf.get(CONF_NAME);
    if (c == null) {
      throw new RemoteException("Cannot open config: "+ CONF_NAME);
    }
    return c;
  }


  /**
   * 打开 product 并返回, 没有权限检查, product 不存在抛出异常
   */
  Document openProduct(String pid) throws RemoteException {
    Document filter = new Document("_id", pid);
    Document p = openDb(TABLE_PRODUCT).find(filter).first();
    if (p == null) {
      throw new RemoteException("Product no exists");
    }
    return p;
  }


  /**
   * 在打开 product 之前进行权限检查, 产品存在返回产品完整 id
   */
  String hasProduct(String paasUser, String scenesid, String productid)
          throws RemoteException {
    String id = id(scenesid, productid);
    MongoDatabase db = checkAuth(paasUser, scenesid);
    Document filter = new Document("_id", id);
    if (db.getCollection(TABLE_PRODUCT).count(filter) != 1) {
      throw new RemoteException("Product not exists");
    }
    return id;
  }


  /**
   * 检查当前用户对 scenes 操作权限, 必须在 http 服务上下文中调用
   * @param paasUser 平台用户名
   * @param scenesid 场景id
   * @return 数据库对象
   * @throws RemoteException 无权操作或数据库错误
   */
  MongoDatabase checkAuth(String paasUser, String scenesid) throws RemoteException  {
    List<Document> or = new ArrayList<>();
    or.add(new Document("owner", paasUser));
    or.add(new Document("share", paasUser));
    Document where = new Document("$or", or);
    MongoDatabase db = openDb();

    if (db.getCollection(TABLE_SCENES).count(where) < 1) {
      throw new RemoteException("无权操作场景 "+ scenesid);
    }
    return db;
  }


  /**
   * 打开并返回 address, 不存在抛出异常
   */
  Document openAddress(String _id) throws RemoteException {
    Document where = new Document("_id", _id);
    FindIterable<Document> res = openDb(TABLE_ADDRESS).find(where);
    Document addr = res.first();
    if (addr == null) {
      throw new RemoteException("Address not exists");
    }
    return addr;
  }


  /**
   * 返回设备账户, 不存在抛出异常, 只能在 http 服务上下文线程中调用
   */
  DeviceUser getUser(String paasUser, String deviceUser) throws RemoteException {
    Document where = new Document("_id", deviceUser);
    where.put("owner", paasUser);
    Document r = openDb("user").find(where).first();
    if (r == null) {
      throw new RemoteException("User not exists");
    }
    return new DeviceUser(deviceUser, r.getString("password"));
  }


  /**
   * 返回脚本对象, 找不到抛出异常
   */
  Document getScript(String id) throws IOException {
    Document where = new Document("_id", id);

    try {
      Document r = openDb(TABLE_SCRIPT).find(where).first();
      if (r == null) {
        throw new IOException("Cannot open script "+ id);
      }
      return r;
    } catch (RemoteException e) {
      throw new IOException(e);
    }
  }


  /**
   * 生成 mq 登录密码
   */
  String genPassword(DeviceUser du) throws RemoteException {
    try {
      byte[] b = new byte[16 + 16];
      rand.nextBytes(b);
      MessageDigest md = MessageDigest.getInstance("md5");
      md.update(b, 0, 16);
      md.update(du.username.getBytes(IConstant.CHARSET));
      md.update(du.password.getBytes(IConstant.CHARSET));
      md.digest(b, 16, 16);
      return b64e.encodeToString(b);

    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   * 打开到 mqtt 服务器的连接
   * @param clientId 客户端id, 不能重复
   * @param username 用户名
   * @param idx 线程索引, 0线程会持久化服务器事务数据
   * @param mc
   * @return 客户端对象
   * @throws RemoteException
   * @throws MqttException
   */
  MqttAsyncClient openMqtt(String clientId, DeviceUser user, int idx, MqttCallback mc)
          throws RemoteException, MqttException
  {
    String broker = getBrokerURL();
    String pass = genPassword(user);

    MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(saveDataPath);
    MqttConnectOptions opt = new MqttConnectOptions();
    MqttAsyncClient cli = new MqttAsyncClient(broker, clientId, persistence);
    cli.setCallback(mc);

    opt.setUserName(user.username);
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
    log.debug("Connected to", broker, "client ID:", clientId, user.username);
    return cli;
  }


  private String getBrokerURL() throws RemoteException {
    Document conf = openConf();
    int port = (int)(double) conf.getDouble("mqport");
    return "tcp://"+ conf.getString("mqhost") +":"+ port;
  }


  /**
   * 打开数据库文档集
   * @param collname
   * @return
   * @throws RemoteException
   */
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


  static String toTopic(String scenes, String product, String device, String type) {
    return '/'+ scenes +'/'+ product +'/'+ device +'/'+ type;
  }


  public static String dataId(String dev, String name, int dt, Calendar d) {
    switch (dt) {
      case DT_YEAR:
        return "!yr~"+ dev +"$"+ name;

      case DT_MONTH:
        return "!mo~"+ dev +'$'+ name
                +'@'+ d.get(Calendar.YEAR);

      case DT_DAY:
        return "!dy~"+ dev +'$'+ name
                +'@'+ d.get(Calendar.YEAR)
                +"-"+ d.get(Calendar.MONTH);

      case DT_HOUR:
        return "!hr~"+ dev +'$'+ name
                +'@'+ d.get(Calendar.YEAR)
                +"-"+ d.get(Calendar.MONTH)
                +"-"+ d.get(Calendar.DAY_OF_MONTH);

      case DT_MINUTE:
        return "!mi~"+ dev +'$'+ name
                +'@'+ d.get(Calendar.YEAR)
                +"-"+ d.get(Calendar.MONTH)
                +"-"+ d.get(Calendar.DAY_OF_MONTH)
                +"_"+ d.get(Calendar.HOUR_OF_DAY);

      case DT_SECOND:
        return "!se~"+ dev +'$'+ name
                +'@'+ d.get(Calendar.YEAR)
                +"-"+ d.get(Calendar.MONTH)
                +"-"+ d.get(Calendar.DAY_OF_MONTH)
                +"_"+ d.get(Calendar.HOUR_OF_DAY)
                +":"+ d.get(Calendar.MINUTE);
    }
    throw new XBosonException("Invalid data type "+ dt);
  }
}
