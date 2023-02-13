/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 18-1-5 上午8:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/MongoImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoDriverInformation;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.xboson.app.AppContext;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.LoginUser;
import com.xboson.db.ConnectConfig;
import com.xboson.script.IVisitByScript;
import com.xboson.script.JSObject;
import com.xboson.util.CreatorFromUrl;
import com.xboson.util.MongoDBPool;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MongoImpl extends RuntimeUnitImpl implements IAResource {

  /**
   * url 类型前缀
   */
  public interface URLTypes {
    /** Mongo url 前缀: mongodb://username:password@localhost:27017,somehost */
    String MONGO = "mongodb";

    /** 数据源 url 前缀: source://sourcekey */
    String SOURCEKEY = "source";
  }

  private CreatorFromUrl<Client> clientCreator;


  public MongoImpl() {
    super(null);
    clientCreator = new CreatorFromUrl<>();

    clientCreator.reg(URLTypes.MONGO, (v, p, url, d) -> {
      return new Client(MongoDBPool.me().get(url));
    });

    clientCreator.reg(URLTypes.SOURCEKEY, (v, p, url, d) -> {
      LoginUser user = (LoginUser) AppContext.me().who();
      ConnectConfig cc = SqlImpl.sourceConfig(v, user.userid);
      return new Client(MongoDBPool.me().get(cc));
    });
  }


  public Client connect(String url) throws Exception {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    return clientCreator.create(url);
  }


  public ObjectId newObjectId(String s) {
    return new ObjectId(s);
  }


  @Override
  public String description() {
    return "app.module.mongodb.functions()";
  }


  public class Cursor implements IVisitByScript {
    private FindIterable<Document> fi;

    private Cursor(FindIterable<Document> fi) {
      this.fi = fi;
    }

    public Cursor projection(Object projection) {
      fi.projection(new JSObjectToBson(projection));
      return this;
    }

    public Cursor filter(Object filter) {
      fi.filter(new JSObjectToBson(filter));
      return this;
    }

    public Cursor limit(int i) {
      fi.limit(i);
      return this;
    }

    public Cursor skip(int i) {
      fi.skip(i);
      return this;
    }

    public Cursor maxTimeMS(long ms) {
      fi.maxTime(ms, TimeUnit.MILLISECONDS);
      return this;
    }

    public Cursor maxAwaitTimeMS(long ms) {
      fi.maxAwaitTime(ms, TimeUnit.MILLISECONDS);
      return this;
    }

    public Cursor sort(Object sort) {
      fi.sort(new JSObjectToBson(sort));
      return this;
    }

    public Cursor partial(boolean partial) {
      fi.partial(partial);
      return this;
    }

    public Cursor hint(Object o) {
      fi.hint(new JSObjectToBson(o));
      return this;
    }

    public Cursor max(Object o) {
      fi.max(new JSObjectToBson(o));
      return this;
    }

    public Cursor min(Object o) {
      fi.min(new JSObjectToBson(o));
      return this;
    }

    public Cursor returnKey(boolean key) {
      fi.returnKey(key);
      return this;
    }

    public Cursor showRecordId(boolean key) {
      fi.showRecordId(key);
      return this;
    }

    public Cursor snapshot(boolean key) {
      fi.snapshot(key);
      return this;
    }

    public Cursor page(SysImpl sys) {
      RequestImpl req = (RequestImpl) sys.request;
      long pageNum  = req.getInteger("pagenum", false, 1);
      long pageSize = req.getInteger("pagesize", false, 1);

      long begin = (pageNum -1) *pageSize;
      fi.skip((int) begin).limit((int) pageSize);
      return this;
    }

    public Object toArray() {
      return toObject(fi);
    }
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

    public Object docs() {
      return all();
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
      return coll.deleteMany(new JSObjectToBson(filter));
    }

    public Object find(Object query) {
      return toObject(coll.find(new JSObjectToBson(query)));
    }

    public Object find(Object query, int pageNum, int pageSize) {
      int begin = (pageNum -1) *pageSize;
      FindIterable<Document> find = coll.find(new JSObjectToBson(query));
      return toObject(find.skip(begin).limit(pageSize));
    }

    public Object find(Object query, Object projection) {
      FindIterable<Document> res = coll.find(new JSObjectToBson(query));
      res.projection(new JSObjectToBson(projection));
      return toObject(res, null);
    }

    public Object find(Object query, Object projection, int pageNum, int pageSize) {
      int begin = (pageNum -1) *pageSize;
      FindIterable<Document> find = coll.find(new JSObjectToBson(query));
      find.projection(new JSObjectToBson(projection));
      return toObject(find.skip(begin).limit(pageSize), null);
    }

    public Object find() {
      return toObject(coll.find());
    }

    public Object find(int pageNum, int pageSize) {
      int begin = (pageNum -1) *pageSize;
      return toObject(coll.find().skip(begin).limit(pageSize));
    }

    public Cursor find2(Object query) {
      FindIterable<Document> find = coll.find(new JSObjectToBson(query));
      return new Cursor(find);
    }

    public Object aggregate(List<Object> query) {
      List<Bson> q = new ArrayList<>(query.size());
      for (int i=0; i<query.size(); ++i) {
        q.set(i, new JSObjectToBson(query.get(i)));
      }
      return toObject(coll.aggregate(q));
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

    public Object count(Object query) {
      return coll.count(new JSObjectToBson(query));
    }
  }


  public class JSObjectToBson implements Bson {
    private ScriptObjectMirror js;
    private BsonDocumentWriter writer;

    private JSObjectToBson(ScriptObjectMirror js) {
      this.js = js;
    }

    private JSObjectToBson(Object o) {
      this.js = wrap(o);
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
      }
      else if (isDate(val)) {
        doc.put(en.getKey(), toDate(val));
      }
      else {
        doc.put(en.getKey(), val);
      }
    }
    return doc;
  }


  /**
   * js 对象转换为多文档格式.
   */
  protected List toManyDoc(Object jsobj) {
    ScriptObjectMirror js = wrap(jsobj);
    final int size = js.size();
    List<Object> docs = new ArrayList<>(size);

    for (int i=0; i<size; ++i) {
      Object o = js.getSlot(i);
      if (o == null) {
        docs.add(null);
      }
      else if (isArray(o)) {
        docs.add(toManyDoc(o));
      }
      else if (o instanceof Map) {
        docs.add(toDoc(o));
      }
      else {
        docs.add(o);
      }
    }
    return docs;
  }


  /**
   * Mongo 结果集转换为 js 数组
   */
  protected Object toObject(Iterable<?> iter) {
    return toObject(iter, null);
  }


  protected Object toObject(Iterable<?> iter, Map projection) {
    ScriptObjectMirror list = createJSList();
    int i = 0;
    for (Object o : iter) {
      list.setSlot(i, toObject(o, projection));
      ++i;
    }
    return unwrap(list);
  }


  /**
   * 判断 mongo 数据类型转换为 js 对象
   */
  protected Object toObject(Object mongoObj) {
    return toObject(mongoObj, null);
  }


  protected Object toObject(Object mongoObj, Map projection) {
    if (mongoObj instanceof Map) {
      return toObject((Map) mongoObj, projection);
    }
    if (mongoObj instanceof Iterable) {
      return toObject((Iterable) mongoObj, projection);
    }
    return mongoObj;
  }


  /**
   * 将 mongo 对象转换为 js 对象
   */
  protected Object toObject(Map<String, Object> doc) {
    return toObject(doc, null);
  }


  /**
   * projection 可以空, 非空时, 只有集合中的属性会被输出
   */
  protected Object toObject(Map<String, Object> doc, Map projection) {
    ScriptObjectMirror obj = createJSObject();

    for (Map.Entry<String, Object> entry : doc.entrySet()) {
      if (projection != null && !isAboutTrue(projection.get(entry.getKey()))) {
        continue;
      }
      // 不传递 projection, 不支持深层 projection.
      obj.setMember(entry.getKey(), toObject(entry.getValue()));
    }
    return unwrap(obj);
  }


  protected DeleteOptions toDelOptions(Object o) {
    ScriptObjectMirror js = wrap(o);
    DeleteOptions opt = new DeleteOptions();
    Object collation = js.getMember("collation");
    if (!isNull(collation)) {
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
    if (!isNull(collation)) {
      opt.collation(toCollation(collation));
    }
    Object arrayFilters = js.getMember("arrayFilters");
    if (!isNull(arrayFilters)) {
      opt.arrayFilters(toManyDoc(arrayFilters));
    }
    Tool.pl(arrayFilters);
    return opt;
  }


  protected IndexOptions toIndexOptions(Object o) {
    ScriptObjectMirror js = wrap(o);
    IndexOptions opt = new IndexOptions();

    Object tmp = js.getMember("collation");
    if (! isNull(tmp)) {
      opt.collation(toCollation(tmp));
    }

    tmp = js.getMember("background");
    if (! isNull(tmp)) {
      opt.background((boolean) tmp);
    }

    tmp = js.getMember("unique");
    if (! isNull(tmp)) {
      opt.unique((boolean) tmp);
    }

    tmp = js.getMember("name");
    if (! isNull(tmp)) {
      opt.name((String) tmp);
    }

    tmp = js.getMember("sparse");
    if (! isNull(tmp)) {
      opt.sparse((boolean) tmp);
    }

    tmp = js.getMember("expireAfterSeconds");
    if (! isNull(tmp)) {
      opt.expireAfter((Long) tmp, TimeUnit.SECONDS);
    }

    tmp = js.getMember("version");
    if (! isNull(tmp)) {
      opt.version((Integer) tmp);
    }

    tmp = js.getMember("weights");
    if (! isNull(tmp)) {
      opt.weights(new JSObjectToBson(tmp));
    }

    tmp = js.getMember("defaultLanguage");
    if (! isNull(tmp)) {
      opt.defaultLanguage((String) tmp);
    }

    tmp = js.getMember("languageOverride");
    if (! isNull(tmp)) {
      opt.languageOverride((String) tmp);
    }

    tmp = js.getMember("textVersion");
    if (! isNull(tmp)) {
      opt.textVersion((Integer) tmp);
    }

    tmp = js.getMember("sphereVersion");
    if (! isNull(tmp)) {
      opt.sphereVersion((Integer) tmp);
    }

    tmp = js.getMember("bits");
    if (! isNull(tmp)) {
      opt.bits((Integer) tmp);
    }

    tmp = js.getMember("min");
    if (! isNull(tmp)) {
      opt.min((Double) tmp);
    }

    tmp = js.getMember("max");
    if (! isNull(tmp)) {
      opt.max((Double) tmp);
    }

    tmp = js.getMember("bucketSize");
    if (! isNull(tmp)) {
      opt.bucketSize((Double) tmp);
    }

    tmp = js.getMember("storageEngine");
    if (! isNull(tmp)) {
      opt.storageEngine(new JSObjectToBson(tmp));
    }

    tmp = js.getMember("partialFilterExpression");
    if (! isNull(tmp)) {
      opt.partialFilterExpression(new JSObjectToBson(tmp));
    }
    return opt;
  }


  protected Collation toCollation(Object c) {
    ScriptObjectMirror coll = wrap(c);
    Collation.Builder b = Collation.builder();

    Object tmp = coll.getMember("locale");
    if (! isNull(tmp)) {
      b.locale((String) tmp);
    }

    tmp = coll.getMember("caseLevel");
    if (! isNull(tmp)) {
      b.caseLevel((boolean) tmp);
    }

    tmp = coll.getMember("caseFirst");
    if (! isNull(tmp)) {
      b.collationCaseFirst(CollationCaseFirst.fromString((String) tmp));
    }

    tmp = coll.getMember("strength");
    if (! isNull(tmp)) {
      b.collationStrength(CollationStrength.fromInt((int) tmp));
    }

    tmp = coll.getMember("numericOrdering");
    if (! isNull(tmp)) {
      b.numericOrdering((boolean) tmp);
    }

    tmp = coll.getMember("alternate");
    if (! isNull(tmp)) {
      b.numericOrdering((boolean) tmp);
    }

    tmp = coll.getMember("maxVariable");
    if (! isNull(tmp)) {
      b.collationMaxVariable(CollationMaxVariable.fromString((String) tmp));
    }

    tmp = coll.getMember("normalization");
    if (! isNull(tmp)) {
      b.normalization((boolean) tmp);
    }

    tmp = coll.getMember("backwards");
    if (! isNull(tmp)) {
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

    if (val instanceof ObjectId) {
      writer.writeObjectId((ObjectId) val);
      return;
    }

    if (isDate(val)) {
      writer.writeDateTime(toDate(val).getTime());
      return;
    }

    Object jsval = unwrap(val);
    if (isRegexp(jsval)) {
      writer.writeRegularExpression(
              new BsonRegularExpression(getRegExp(jsval).source) );
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
    for (Map.Entry<String, Object> et : s.entrySet()) {
      writer.writeName(et.getKey());
      writeValue(writer, et.getValue());
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


  /**
   * TODO: 实现该类, 跳过对 ScriptObjectMirror 和 Document 之间的数据转换
   *
   * mongo 驱动使用 org.bson.Document 作为中间值传递数据, 改编码器跳过转换过程,
   * 直接把 js 对象中的数据输出到驱动层.
   */
  public class JsObjectMongoDataCodec implements Codec<ScriptObjectMirror> {
    private CodecRegistry codec;


    /**
     * @param defaultCodec MongoDatabase.getCodecRegistry() 可以获取默认编码器
     *                     注册器, 用来处理默认数据结构
     */
    private JsObjectMongoDataCodec(CodecRegistry defaultCodec) {
      this.codec = defaultCodec;
    }


    @Override
    public ScriptObjectMirror decode(BsonReader r, DecoderContext ctx) {
      return (ScriptObjectMirror) readObj(r, ctx);
    }


    private Object readArray(BsonReader r, DecoderContext ctx) {
      ScriptObjectMirror obj = createJSList();
      int i = 0;
      do {
        obj.setSlot(i++, readObj(r, ctx));
      } while (r.readBsonType() != BsonType.END_OF_DOCUMENT);
      return obj;
    }


    private Object readMap(BsonReader r, DecoderContext ctx) {
      ScriptObjectMirror obj = createJSObject();
      do {
        obj.put(r.readName(), readObj(r, ctx));
      } while (r.readBsonType() != BsonType.END_OF_DOCUMENT);
      return obj;
    }


    private Object readObj(BsonReader r, DecoderContext ctx) {
      switch (r.readBsonType()) {
        case DOCUMENT:
          return readMap(r, ctx);

        case ARRAY:
          return readArray(r, ctx);

        case DOUBLE:
          return r.readDouble();

        case STRING:
          return r.readString();

        case UNDEFINED:
          return nullObj();

        case BOOLEAN:
          return r.readBoolean();

        case DATE_TIME:
          return new Date(r.readDateTime());

        case NULL:
          return null;

        case INT32:
          return r.readInt32();

        case INT64:
          return r.readInt64();


        case TIMESTAMP:
          return new Date(r.readTimestamp().getTime()*1000);

        // TODO: 针对 mongodb 的对象进行包装
        case DECIMAL128:
        case JAVASCRIPT_WITH_SCOPE:
        case SYMBOL:
        case JAVASCRIPT:
        case DB_POINTER:
        case REGULAR_EXPRESSION:
        case BINARY:
        case OBJECT_ID:
        case MAX_KEY:
        case MIN_KEY:

        default:
          throw new IllegalStateException(r.readBsonType().name());
      }
    }


    @Override
    public void encode(BsonWriter w, ScriptObjectMirror jobj,
                       EncoderContext ctx) {
      writeObj(w, jobj, ctx);
    }


    private void writeObj(BsonWriter w, Object obj, EncoderContext ctx) {
      if (obj == null) {
        w.writeNull();
      }
      else if (obj instanceof ScriptObjectMirror) {
        writeObj(w, (ScriptObjectMirror)obj, ctx);
      }
      else {
        Codec c = codec.get(obj.getClass());
        ctx.encodeWithChildContext(c, w, obj);
      }
    }


    private void writeObj(BsonWriter w, ScriptObjectMirror jobj,
                          EncoderContext ctx) {
      if (jobj.isArray()) {
        writeArray(w, jobj, ctx);
      }
      else if (isNull(jobj) || jobj.isFunction()) {
        w.writeNull();
      }
      else if (isRegexp(jobj)) {
        JsRegExp jre = getRegExp(jobj);
        BsonRegularExpression exp = new BsonRegularExpression(jre.source, jre.options());
        w.writeRegularExpression(exp);
      }
      else {
        writeMap(w, jobj, ctx);
      }
    }

    private void writeMap(BsonWriter w, ScriptObjectMirror jobj,
                          EncoderContext ctx) {
      w.writeStartDocument();
      for (Map.Entry<String, Object> et : jobj.entrySet()) {
        w.writeName(et.getKey());
        writeObj(w, et.getValue(), ctx);
      }
      w.writeEndDocument();
    }


    private void writeArray(BsonWriter w, ScriptObjectMirror jobj,
                            EncoderContext ctx) {
      w.writeStartArray();
      final int size = jobj.size();
      for (int i=0; i<size; ++i) {
        writeObj(w, jobj.getSlot(i), ctx);
      }
      w.writeEndArray();
    }


    @Override
    public Class<ScriptObjectMirror> getEncoderClass() {
      return ScriptObjectMirror.class;
    }
  }
}
