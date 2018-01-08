////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-5 上午8:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/MongoImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.xboson.util.MongoDBPool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptObject;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MongoImpl extends RuntimeUnitImpl {

  public MongoImpl() {
    super(null);
  }


  public Client connect(String url) {
    MongoDBPool.VirtualMongoClient client = MongoDBPool.me().get(url);
    return new Client(client);
  }


  public class Client implements AutoCloseable {
    private final MongoDBPool.VirtualMongoClient client;

    private Client(MongoDBPool.VirtualMongoClient client) {
      this.client = client;
      ModuleHandleContext.autoClose(this);
    }

    public Database db(String name) {
      return new Database(client.getDatabase(name));
    }

    public Object dbs() {
      return toObject(client.listDatabaseNames());
    }

    public Object all() {
      return dbs();
    }

    @Override
    public void close() throws Exception {
      client.close();
    }
  }


  public class Database {
    private final MongoDatabase db;

    private Database(MongoDatabase db) {
      this.db = db;
    }

    public Object all() {
      return toObject(db.listCollectionNames());
    }

    public Collection collection(String name) {
      return new Collection(db.getCollection(name));
    }
  }


  public class Collection {
    private MongoCollection<Document> coll;

    private Collection(MongoCollection<Document> coll) {
      this.coll = coll;
    }


    public void insert(Object js) {
      if (isArray(js)) {
        coll.insertMany(toManyDoc(js));
      } else {
        coll.insertOne(toDoc(js));
      }
    }

    public void insertMany(Object js) {
      coll.insertMany(toManyDoc(js));
    }

    public void insertOne(Object js) {
      coll.insertOne(toDoc(js));
    }


    public UpdateResult updateOne(Object filter, Object update, Object options) {
      return coll.updateOne(new JSObjectToBson(filter),
              new JSObjectToBson(update), toUpOptions(options));
    }

    public UpdateResult updateOne(Object filter, Object update) {
      return coll.updateOne(new JSObjectToBson(filter), new JSObjectToBson(update));
    }

    public UpdateResult updateMany(Object filter, Object update, Object options) {
      return coll.updateMany(new JSObjectToBson(filter),
              new JSObjectToBson(update), toUpOptions(options));
    }

    public UpdateResult updateMany(Object filter, Object update) {
      return coll.updateMany(new JSObjectToBson(filter), new JSObjectToBson(update));
    }

    public UpdateResult replaceOne(Object filter, Object replacement, Object options) {
      return coll.replaceOne(new JSObjectToBson(filter),
              toDoc(replacement), toUpOptions(options));
    }

    public UpdateResult replaceOne(Object filter, Object replacement) {
      return coll.replaceOne(new JSObjectToBson(filter), toDoc(replacement));
    }


    public DeleteResult deleteOne(Object filter, Object options) {
      return coll.deleteOne(new JSObjectToBson(filter), toDelOptions(options));
    }

    public DeleteResult deleteOne(Object filter) {
      return coll.deleteOne(new JSObjectToBson(filter));
    }

    public DeleteResult deleteMany(Object filter, Object options) {
      return coll.deleteOne(new JSObjectToBson(filter), toDelOptions(options));
    }

    public DeleteResult deleteMany(Object filter) {
      return coll.deleteOne(new JSObjectToBson(filter));
    }


    public Object find(Object query) {
      return toObject(coll.find(new JSObjectToBson(query)));
    }

    public Object find() {
      return toObject(coll.find());
    }


    public String createIndex(Object key, Object options) {
      return coll.createIndex(new JSObjectToBson(key), toIndexOptions(options));
    }

    public String createIndex(Object key) {
      return coll.createIndex(new JSObjectToBson(key));
    }

    public void dropIndex(Object doc) {
      coll.dropIndex(new JSObjectToBson(doc));
    }

    public void dropIndex(String name) {
      coll.dropIndex(name);
    }

    public Object getIndexes() {
      return toObject(coll.listIndexes());
    }


    public void drop() {
      coll.drop();
    }

    public long count() {
      return coll.count();
    }
  }


  public class JSObjectToBson implements Bson {
    private ScriptObjectMirror js;
    private BsonDocumentWriter writer;

    private JSObjectToBson(ScriptObjectMirror js) {
      this.js = js;
    }

    private JSObjectToBson(Object o) {
      this(wrap(o));
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(
            Class<TDocument> aClass, CodecRegistry codecRegistry) {
      if (writer == null) {
        writer = new BsonDocumentWriter(new BsonDocument());
        writeValue(writer, js);
      }
      return writer.getDocument();
    }
  }


  protected boolean isArray(Object jsobj) {
    if (jsobj instanceof ScriptObject) {
      return ((ScriptObject) jsobj).isArray();
    } else if (jsobj instanceof ScriptObjectMirror) {
      return ((ScriptObjectMirror) jsobj).isArray();
    }
    return false;
  }


  /**
   * js 对象转换为 Mongo 文档对象
   */
  protected Document toDoc(Object jsobj) {
    ScriptObjectMirror js = wrap(jsobj);
    Document doc = new Document();
    for (Map.Entry<String, Object> en : js.entrySet()) {
      Object val = en.getValue();
      if (isArray(val)) {
        doc.put(en.getKey(), toManyDoc(val));
      } else {
        doc.put(en.getKey(), val);
      }
    }
    return doc;
  }


  /**
   * js 对象转换为多文档格式.
   */
  protected List<Document> toManyDoc(Object jsobj) {
    ScriptObjectMirror js = wrap(jsobj);
    final int size = js.size();
    List<Document> docs = new ArrayList<>(size);
    for (int i=0; i<size; ++i) {
      Object o = js.getSlot(i);
      docs.add(toDoc(o));
    }
    return docs;
  }


  /**
   * Mongo 结果集转换为 js 数组
   */
  protected Object toObject(Iterable<?> iter) {
    ScriptObjectMirror list = createJSList();
    int i = 0;
    for (Object o : iter) {
      list.setSlot(i, toObject(o));
      ++i;
    }
    return unwrap(list);
  }


  /**
   * 判断 mongo 数据类型转换为 js 对象
   */
  protected Object toObject(Object mongoObj) {
    if (mongoObj instanceof Map) {
      return toObject((Map) mongoObj);
    }
    if (mongoObj instanceof Iterable) {
      return toObject((Iterable) mongoObj);
    }
    return mongoObj;
  }


  /**
   * 将 mongo 对象转换为 js 对象
   */
  protected Object toObject(Map<String, Object> doc) {
    ScriptObjectMirror obj = createJSObject();

    for (Map.Entry<String, Object> entry : doc.entrySet()) {
      obj.setMember(entry.getKey(), toObject(entry.getValue()));
    }
    return unwrap(obj);
  }


  protected DeleteOptions toDelOptions(Object o) {
    ScriptObjectMirror js = wrap(o);
    DeleteOptions opt = new DeleteOptions();
    Object collation = js.getMember("collation");
    if (collation != null) {
      opt.collation(toCollation(collation));
    }
    return opt;
  }


  protected UpdateOptions toUpOptions(Object jsobj) {
    ScriptObjectMirror js = wrap(jsobj);
    UpdateOptions opt = new UpdateOptions();
    if (isTrue(js.getMember("upsert"))) {
      opt.upsert(true);
    }
    Object collation = js.getMember("collation");
    if (collation != null) {
      opt.collation(toCollation(collation));
    }
    return opt;
  }


  protected IndexOptions toIndexOptions(Object o) {
    ScriptObjectMirror js = wrap(o);
    IndexOptions opt = new IndexOptions();
    Object tmp = js.getMember("collation");
    if (tmp != null) {
      opt.collation(toCollation(tmp));
    }

    tmp = js.getMember("background");
    if (tmp != null) {
      opt.background((boolean) tmp);
    }

    tmp = js.getMember("unique");
    if (tmp != null) {
      opt.unique((boolean) tmp);
    }

    tmp = js.getMember("name");
    if (tmp != null) {
      opt.name((String) tmp);
    }

    tmp = js.getMember("sparse");
    if (tmp != null) {
      opt.sparse((boolean) tmp);
    }

    tmp = js.getMember("expireAfterSeconds");
    if (tmp != null) {
      opt.expireAfter((Long) tmp, TimeUnit.SECONDS);
    }

    tmp = js.getMember("version");
    if (tmp != null) {
      opt.version((Integer) tmp);
    }

    tmp = js.getMember("weights");
    if (tmp != null) {
      opt.weights(new JSObjectToBson(tmp));
    }

    tmp = js.getMember("defaultLanguage");
    if (tmp != null) {
      opt.defaultLanguage((String) tmp);
    }

    tmp = js.getMember("languageOverride");
    if (tmp != null) {
      opt.languageOverride((String) tmp);
    }

    tmp = js.getMember("textVersion");
    if (tmp != null) {
      opt.textVersion((Integer) tmp);
    }

    tmp = js.getMember("sphereVersion");
    if (tmp != null) {
      opt.sphereVersion((Integer) tmp);
    }

    tmp = js.getMember("bits");
    if (tmp != null) {
      opt.bits((Integer) tmp);
    }

    tmp = js.getMember("min");
    if (tmp != null) {
      opt.min((Double) tmp);
    }

    tmp = js.getMember("max");
    if (tmp != null) {
      opt.max((Double) tmp);
    }

    tmp = js.getMember("bucketSize");
    if (tmp != null) {
      opt.bucketSize((Double) tmp);
    }

    tmp = js.getMember("storageEngine");
    if (tmp != null) {
      opt.storageEngine(new JSObjectToBson(tmp));
    }

    tmp = js.getMember("partialFilterExpression");
    if (tmp != null) {
      opt.partialFilterExpression(new JSObjectToBson(tmp));
    }
    return opt;
  }


  protected Collation toCollation(Object c) {
    ScriptObjectMirror coll = wrap(c);
    Collation.Builder b = Collation.builder();

    Object tmp = coll.getMember("locale");
    if (tmp != null) {
      b.locale((String) tmp);
    }

    tmp = coll.getMember("caseLevel");
    if (tmp != null) {
      b.caseLevel((boolean) tmp);
    }

    tmp = coll.getMember("caseFirst");
    if (tmp != null) {
      b.collationCaseFirst(CollationCaseFirst.fromString((String) tmp));
    }

    tmp = coll.getMember("strength");
    if (tmp != null) {
      b.collationStrength(CollationStrength.fromInt((int) tmp));
    }

    tmp = coll.getMember("numericOrdering");
    if (tmp != null) {
      b.numericOrdering((boolean) tmp);
    }

    tmp = coll.getMember("alternate");
    if (tmp != null) {
      b.numericOrdering((boolean) tmp);
    }

    tmp = coll.getMember("maxVariable");
    if (tmp != null) {
      b.collationMaxVariable(CollationMaxVariable.fromString((String) tmp));
    }

    tmp = coll.getMember("normalization");
    if (tmp != null) {
      b.normalization((boolean) tmp);
    }

    tmp = coll.getMember("backwards");
    if (tmp != null) {
      b.backwards((boolean) tmp);
    }
    return b.build();
  }


  private void writeValue(BsonDocumentWriter writer, Object val) {
    if (val == null || isNull(val)) {
      writer.writeNull();
      return;
    }

    if (val instanceof Double || val instanceof Float) {
      writer.writeDouble((double) val);
      return;
    }

    if (val instanceof Long) {
      writer.writeInt64((long) val);
      return;
    }

    if (val instanceof Integer) {
      writer.writeInt32((int) val);
      return;
    }

    if (val instanceof String) {
      writer.writeString((String) val);
      return;
    }

    if (val instanceof Date) {
      writer.writeDateTime(((Date) val).getTime());
      return;
    }

    if (val instanceof Boolean) {
      writer.writeBoolean((boolean) val);
      return;
    }

    ScriptObjectMirror som = wrap(val);
    if (som.isArray()) {
      writeArray(writer, som);
    } else {
      writeMap(writer, som);
    }
  }


  private void writeMap(BsonDocumentWriter writer, ScriptObjectMirror s) {
    writer.writeStartDocument();
    for (String name : s.keySet()) {
      writer.writeName(name);
      writeValue(writer, s.getMember(name));
    }
    writer.writeEndDocument();
  }

  private void writeArray(BsonDocumentWriter writer, ScriptObjectMirror s) {
    writer.writeStartArray();
    final int size = s.size();
    for (int i=0; i<size; ++i) {
      writeValue(writer, s.getSlot(i));
    }
    writer.writeEndArray();
  }
}