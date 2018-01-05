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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.Map;


public class MongoImpl extends RuntimeUnitImpl {

  public MongoImpl() {
    super(null);
  }


  public Client connect(String url) {
    MongoClientURI u = new MongoClientURI(url);
    MongoClient client = new MongoClient(u);
    return new Client(client);
  }


  public class Client implements AutoCloseable {
    private final MongoClient client;

    private Client(MongoClient client) {
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

    public Object find(Object o) {
      return toObject(coll.find(new JSObjectToBson(o)));
    }

    public Object find() {
      return toObject(coll.find());
    }

    public void insert(Object o) {
      coll.insertOne(new JSObjectToBson(o).toBsonDocument());
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
        writeValue(js);
      }
      return writer.getDocument();
    }

    private void writeMap(ScriptObjectMirror s) {
      writer.writeStartDocument();
      for (String name : s.keySet()) {
        writer.writeName(name);
        writeValue(s.getMember(name));
      }
      writer.writeEndDocument();
    }

    private void writeArray(ScriptObjectMirror s) {
      writer.writeStartArray();
      final int size = s.size();
      for (int i=0; i<size; ++i) {
        writeValue(s.getSlot(i));
      }
      writer.writeEndArray();
    }

    private void writeValue(Object val) {
      if (val == null || isNull(val)) {
        writer.writeNull();
        return;
      }

      if (val instanceof Double) {
        writer.writeDouble((double) val);
        return;
      }

      if (val instanceof Long) {
        writer.writeInt64((long) val);
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

      ScriptObjectMirror som = wrap(val);
      if (som.isArray()) {
        writeArray(som);
      } else {
        writeMap(som);
      }
    }
  }


  /**
   * Mongo 结果集转换为数组
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


  protected Object toObject(Object o) {
    if (o instanceof Map) {
      return toObject((Map) o);
    }
    if (o instanceof Iterable) {
      return toObject((Iterable) o);
    }
    return o;
  }


  protected Object toObject(Map<String, Object> doc) {
    ScriptObjectMirror obj = createJSObject();

    for (Map.Entry<String, Object> entry : doc.entrySet()) {
      obj.setMember(entry.getKey(), toObject(entry.getValue()));
    }
    return unwrap(obj);
  }
}
