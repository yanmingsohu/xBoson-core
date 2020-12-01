////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-7 下午12:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/MongoDBPool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.mongodb.*;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.session.ClientSession;
import com.xboson.been.MongoConfig;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.event.OnExitHandle;
import com.xboson.event.timer.ResourceCleanup;
import com.xboson.util.c0nst.IConstant;
import org.bson.Document;

import java.util.*;


/**
 * MongoDB 连接池.
 * 配置来自 dbpool
 */
public class MongoDBPool extends OnExitHandle {

  private static final int MONGODB_DB_TYPE = 1000;
  private static MongoDBPool instance;
  private final Map<UriKey, MongoClient> pool;
  //private final ResourceCleanup<UriKey> rc;


  private MongoDBPool() {
    pool = new HashMap<>();
    // 需要大量工作才能让 mongo 支持资源回收, 架构需要改变
    //rc = new ResourceCleanup<>("mongo-pool", pool);
  }


  @Override
  protected void exit() {
    for (MongoClient cli : pool.values()) {
      cli.close();
    }
    pool.clear();
  }


  public static MongoDBPool me() {
    if (instance == null) {
      synchronized (MongoDBPool.class) {
        if (instance == null) {
          instance = new MongoDBPool();
        }
      }
    }
    return instance;
  }


  private MongoClient create(UriKey uri) {
    List<ServerAddress> seedList = new ArrayList<>(uri.getHosts().size());
    for (String host : uri.getHosts()) {
      seedList.add(new ServerAddress(host));
    }

    MongoClientOptions opt = uri.getOptions();
    opt = MongoClientOptions.builder(opt)
            .connectTimeout(IConstant.RESPONSE_ACCEPTABLE_TIMEOUT)
            .serverSelectionTimeout(IConstant.RESPONSE_ACCEPTABLE_TIMEOUT)
            .build();

    MongoCredential cert = uri.getCredentials();
    if (cert == null) {
      return new MongoClient(seedList, opt);
    } else {
      return new MongoClient(seedList, cert, opt);
    }
  }


  /**
   * 返回的对象可以长期保存, 并始终保持打开状态
   * @param _url
   * @return
   */
  public VirtualMongoClient get(String _url) {
    try {
      synchronized (pool /*rc.lock*/) {
        UriKey uri = new UriKey(_url);
        MongoClient client = pool.get(uri);
        if (client == null) {
          client = create(uri);
          pool.put(uri, client);
        }
        return new VirtualMongoClient(uri, client);
      }
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 用 mongo 配置, 打开客户端连接.
   * 返回的对象可以长期保存, 并始终保持打开状态
   */
  public VirtualMongoClient get(MongoConfig mc) {
    if (!mc.enable) {
      throw new XBosonException("Default Mongodb Config disabled");
    }

    StringBuilder buf = new StringBuilder("mongodb://");
    if (Tool.notNulStr(mc.username) && Tool.notNulStr(mc.password)) {
      buf.append(mc.username).append(':').append(mc.password).append('@');
    }
    buf.append(mc.host);
    if (mc.port > 0) {
      buf.append(':').append(mc.port);
    }
    return get(buf.toString());
  }


  /**
   * 使用数据源配置打开客户端连接
   * 返回的对象可以长期保存, 并始终保持打开状态
   */
  public VirtualMongoClient get(ConnectConfig conf) {
    if (conf.getDbid() != MONGODB_DB_TYPE) {
      throw new XBosonException.BadParameter(
              "dbid="+conf.getDbid(),
              "Is not Mongodb ("+MONGODB_DB_TYPE+')');
    }

    StringBuilder buf = new StringBuilder("mongodb://");
    String user = conf.getUsername();
    String ps   = conf.getPassword();
    if (Tool.notNulStr(user) && Tool.notNulStr(ps)) {
      buf.append(user).append(':').append(ps).append('@');
    }
    buf.append(conf.getHost());
    if (conf.getIntPort(0) > 0) {
      buf.append(':').append(conf.getPort());
    }
    String base = conf.getDatabase();
    if (Tool.notNulStr(base)) {
      buf.append('/').append(base);
    }
    return get(buf.toString());
  }


  private class UriKey extends MongoClientURI implements ResourceCleanup.IKeyTime {

    private long last;


    private UriKey(String uri) {
      super(uri);
      last = System.currentTimeMillis();
    }


    @Override
    public boolean equals(Object o) {
      last = System.currentTimeMillis();
      return super.equals(o);
    }


    @Override
    public void release(Object v) {
      ((MongoClient)v).close();
    }


    @Override
    public long freezingTime() {
      return System.currentTimeMillis() - last;
    }
  }


  /**
   * 由 com.mongodb.MongoClient 生成,
   * 与 MongoClient 中的函数签名一致, 只导出使用的函数.
   * MongoDB Version 3.6
   */
  public class VirtualMongoClient implements AutoCloseable {

    private MongoClientURI uri;
    private MongoClient client;


    // MongoClient 内部会管理连接池, 什么都不要做
    public void close() {
      client.close();
    }


    public VirtualMongoClient(MongoClientURI uri, MongoClient original) {
      this.client = original;
      this.uri = uri;
    }


    public MongoClientOptions getMongoClientOptions() {
      return client.getMongoClientOptions();
    }


    public List<MongoCredential> getCredentialsList() {
      return client.getCredentialsList();
    }


    public MongoIterable<String> listDatabaseNames() {
      return client.listDatabaseNames();
    }


    public MongoIterable<String> listDatabaseNames(
            ClientSession clientSession) {
      return client.listDatabaseNames(clientSession);
    }


    public ListDatabasesIterable<Document> listDatabases() {
      return client.listDatabases();
    }


    public <T> ListDatabasesIterable<T> listDatabases(Class<T> clazz) {
      return client.listDatabases(clazz);
    }


    public ListDatabasesIterable<Document> listDatabases(
            ClientSession clientSession) {
      return client.listDatabases(clientSession);
    }


    public <T> ListDatabasesIterable<T> listDatabases(
            ClientSession clientSession, Class<T> clazz) {
      return client.listDatabases(clientSession, clazz);
    }


    public MongoDatabase getDatabase(String databaseName) {
      return client.getDatabase(databaseName);
    }


    public ClientSession startSession(ClientSessionOptions options) {
      return client.startSession(options);
    }


    public WriteConcern getWriteConcern() {
      return client.getWriteConcern();
    }


    public ReadConcern getReadConcern() {
      return client.getReadConcern();
    }


    public List<ServerAddress> getAllAddress() {
      return client.getAllAddress();
    }


    public List<ServerAddress> getServerAddressList() {
      return client.getServerAddressList();
    }


    public ServerAddress getAddress() {
      return client.getAddress();
    }


    public ReplicaSetStatus getReplicaSetStatus() {
      return client.getReplicaSetStatus();
    }


    public Collection<DB> getUsedDatabases() {
      return client.getUsedDatabases();
    }


    public void dropDatabase(String dbName) {
      client.dropDatabase(dbName);
    }


    public CommandResult fsync(boolean async) {
      return client.fsync(async);
    }


    public CommandResult fsyncAndLock() {
      return client.fsyncAndLock();
    }


    public DBObject unlock() {
      return client.unlock();
    }


    public boolean isLocked() {
      return client.isLocked();
    }


    public String toString() {
      return client.toString();
    }


    public int getMaxBsonObjectSize() {
      return client.getMaxBsonObjectSize();
    }


    public String getConnectPoint() {
      return client.getConnectPoint();
    }
  }
}
