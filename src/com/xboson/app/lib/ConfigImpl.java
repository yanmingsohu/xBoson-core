////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-12-10 上午10:57
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ConfigImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xboson.app.AppContext;
import com.xboson.been.XBosonException;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.util.MongoDBPool;
import com.xboson.util.SysConfig;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;

import javax.naming.event.NamingEvent;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 配置工具, 画面由平台提供, 使用系统 mongodb 数据源;
 * 同一个配置名称下, 在不同的场景(模式)返回对应的配置数据.
 */
public class ConfigImpl extends RuntimeUnitImpl {

  public final String DB_NAME   = "intelligent-config";

  public final String ATTR_MODE = "mode";
  public final String ATTR_VAL  = "V";
  public final String ATTR_NAME = "name";
  public final String ATTR_KEY  = "_id";
  public final String ATTR_OWNER= "owner";
  public final String ATTR_D_KEY= "___id";

  public final String COLL_META = "meta";
  public final String COLL_DATA = "data";

  public final int MODE_USER    = 1;
  public final int MODE_ORG     = 2;
  public final int MODE_APP     = 3;
  public final int MODE_GLOBAL  = 4;
  public final int MODE_ROLE    = 5;

  private static final Cache cache = new Cache();
  public MongoDatabase __db;


  public ConfigImpl() {
    super(null);
  }


  /**
   * 虽然不创建索引也可以使用, 当效率降低时则需要启用索引.
   */
  public void ___init() {
    MongoCollection<Document> dataColl = openDB().getCollection(COLL_DATA);
    BsonDocument dindex = new BsonDocument();
    dindex.put(ATTR_D_KEY, new BsonInt32(1));
    dataColl.createIndex(dindex);

    MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
    BsonDocument mindex = new BsonDocument();
    mindex.put(ATTR_NAME, new BsonInt32(1));
    metaColl.createIndex(dindex);
  }


  public Object get(final String name) {
    Document meta = meta(name);
    if (meta == null) {
      throw new XBosonException("Config not exists:"+ name);
    }

    BsonString dataKeyS = dataKey(name, meta);
    Document ret = cache.getData(dataKeyS);
    if (ret != null)
      return ret;

    try {
      cache.wr.lock();
      BsonDocument find = new BsonDocument();
      find.put(ATTR_D_KEY, dataKeyS);
      MongoCollection<Document> dataColl = openDB().getCollection(COLL_DATA);
      FindIterable<Document> fi = dataColl.find(find);
      ret = fi.first();
      if (ret == null) {
        return createJSObject();
      }
      ret.remove(ATTR_D_KEY);
      ret.remove(ATTR_KEY);
      cache.putData(dataKeyS, ret);
      return ret;
    } finally {
      cache.wr.unlock();
    }
  }


  public void set(String name, Map<String, Object> val) {
    Document meta = meta(name);
    if (meta == null) {
      throw new XBosonException("Config not exists:"+ name);
    }

    try {
      cache.wr.lock();
      BsonString dataKeyS = dataKey(name, meta);
      cache.sendDataChange(dataKeyS);
      MongoCollection<Document> dataColl = openDB().getCollection(COLL_DATA);
      val.put(ATTR_D_KEY, dataKeyS);
      BsonDocument del = new BsonDocument();
      del.put(ATTR_D_KEY, dataKeyS);
      dataColl.deleteMany(del);
      dataColl.insertOne(new Document(val));
    } finally {
      cache.wr.unlock();
    }
  }


  public void create(Map<String, Object> config) {
    String name = (String) config.get(ATTR_NAME);
    if (name == null) {
      throw new XBosonException.BadParameter(ATTR_NAME, "null");
    }
    long mode = (Long) config.get(ATTR_MODE);
    if (mode < 0 || mode > 5) {
      throw new XBosonException.BadParameter(ATTR_MODE, "bad value");
    }

    SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
    config.put(ATTR_OWNER, sys.getUserIdByOpenId());

    MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
    BsonDocument find = new BsonDocument();
    find.put(ATTR_NAME, new BsonString(name));
    if (metaColl.find(find).first() != null) {
      throw new XBosonException("Config is exists:"+ name);
    }

    metaColl.insertOne(new Document(config));
  }


  public Object list(int pagenum, int pagesize, Map<String, Object> condition) {
    MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
    ScriptObjectMirror ret = createJSList();
    int i = 0;
    int begin = (pagenum-1)*pagesize;
    FindIterable<Document> find = metaColl.find(new Document(condition));

    for (Document doc : find.skip(begin).limit(pagesize)) {
      ScriptObjectMirror item = createJSObject();
      item.putAll(doc);
      ret.setSlot(i++, item);
    }
    return ret;
  }


  public Object size(Map<String, Object> condition) {
    MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
    return metaColl.count(new Document(condition));
  }


  public void remove(String name) {
    MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
    BsonDocument find = new BsonDocument();
    find.put(ATTR_NAME, new BsonString(name));
    Document ret = metaColl.find(find).first();
    if (ret == null) {
      throw new XBosonException("Config not found:"+ name);
    }
    SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
    if (! ret.get(ATTR_OWNER).equals(sys.getUserIdByOpenId())) {
      throw new XBosonException("Only the creator can delete");
    }
    metaColl.findOneAndDelete(find);
    cache.sendMetaModify(name);
  }


  private BsonString dataKey(String name, Document meta) {
    String id = null;
    int mode = meta.getLong(ATTR_MODE).intValue();
    switch (mode) {

      case MODE_USER:
        SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
        id = "U:"+ sys.getUserPID() +":"+ name;
        break;

      case MODE_ORG:
        id = "O:"+ AppContext.me().originalOrg() +":"+ name;
        break;

      case MODE_APP:
        id = "A:"+ AppContext.me().getExtendParameter().get("app") +":"+ name;
        break;

      case MODE_GLOBAL:
        id = "G:"+ name;
        break;

      case MODE_ROLE:
        throw new UnsupportedOperationException("role mode");

      default:
        throw new UnsupportedOperationException("Mode "+ mode);
    }
    return new BsonString(id);
  }


  /**
   * 获取元数据, 带本地缓存
   */
  private Document meta(String name) {
    Document meta = cache.getMeta(name);
    if (meta != null)
      return meta;

    try {
      cache.wr.lock();
      MongoCollection<Document> metaColl = openDB().getCollection(COLL_META);
      BsonDocument find = new BsonDocument();
      find.put(ATTR_NAME, new BsonString(name));
      FindIterable<Document> ret = metaColl.find(find);
      meta = ret.first();

      cache.putMeta(name, meta);
      return meta;
    } finally {
      cache.wr.unlock();
    }
  }


  private MongoDatabase openDB() {
    if (__db == null) {
      synchronized (this) {
        if (__db == null) {
          MongoDBPool.VirtualMongoClient cli =
                  MongoDBPool.me().get(SysConfig.me().readConfig().mongodb);
          ModuleHandleContext.autoClose(cli);
          __db = cli.getDatabase(DB_NAME);
        }
      }
    }
    return __db;
  }


  private static class Cache extends GLHandle {
    private final static int META = 1;
    private final static int DATA = 2;

    private Map<String, Document> _mc;
    private Map<BsonString, Document> _dc;
    private GlobalEventBus bus;
    private ReadWriteLock lock;
    private Lock rd, wr;


    private Cache() {
      _mc  = new WeakHashMap<>();
      _dc  = new WeakHashMap<>();
      lock = new ReentrantReadWriteLock();
      rd   = lock.readLock();
      wr   = lock.writeLock();
      bus  = GlobalEventBus.me();
      bus.on(Names.iconfig_update, this);
    }

    void sendMetaModify(String name) {
      bus.emit(Names.iconfig_update, name, META);
    }

    void sendDataChange(BsonString key) {
      bus.emit(Names.iconfig_update, key, DATA);
    }

    Document getMeta(String name) {
      try {
        rd.lock();
        return _mc.get(name);
      } finally {
        rd.unlock();
      }
    }

    void putMeta(String name, Document m) {
      _mc.put(name, m);
    }

    Document getData(BsonString key) {
      try {
        rd.lock();
        return _dc.get(key);
      } finally {
        rd.unlock();
      }
    }

    void putData(BsonString key, Document d) {
      _dc.put(key, d);
    }

    @Override
    public void objectChanged(NamingEvent namingEvent) {
      try {
        wr.lock();
        switch (namingEvent.getType()) {
          case META:
            _mc.remove(namingEvent.getNewBinding().getObject());
            break;

          case DATA:
            _dc.remove(namingEvent.getNewBinding().getObject());
            break;
        }
      } finally {
        wr.unlock();
      }
    }
  }
}
