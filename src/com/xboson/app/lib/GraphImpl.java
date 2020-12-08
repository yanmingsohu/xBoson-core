////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-30 下午12:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/GraphImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.XBosonException;
import com.xboson.script.IVisitByScript;
import com.xboson.util.CreatorFromUrl;
import com.xboson.util.Neo4jPool;
import org.neo4j.driver.*;
import org.neo4j.driver.types.*;
import org.neo4j.driver.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphImpl extends RuntimeUnitImpl implements IVisitByScript, IAResource {

  public static final String NODE_TYPE_FIELD = "$type";


  /**
   * https://neo4j.com/docs/driver-manual/current/client-applications/#driver-connection-uris
   */
  interface URI {
    /** bolt://localhost:7687 */
    String bolt = "bolt";
    /** neo4j://x.example.com */
    String neo4j = "neo4j";
  }


  private CreatorFromUrl<ISession> driverCreator;


  public GraphImpl() {
    super(null);
    driverCreator = new CreatorFromUrl<>();

    driverCreator.reg(URI.bolt, (v, p, url, d)->{
      return openNeo4j(url, d);
    });

    driverCreator.reg(URI.neo4j, (v, p, url, d)->{
      return openNeo4j(url, d);
    });
  }


  private ISession openNeo4j(String url, Object d) {
    Driver dri = null;
    if (d != null) {
      String[] auth = (String[]) d;
      dri = Neo4jPool.me().open(url, auth[0], auth[1]);
    } else {
      dri = Neo4jPool.me().open(url);
    }
    ISession session = new Neo4jSession(dri.session());
    ModuleHandleContext.autoClose(session);
    return session;
  }


  @Override
  public String description() {
    return "app.module.graph.functions()";
  }


  public ISession connect(String uri) {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    return driverCreator.create(uri);
  }


  public ISession connect(String uri, String username, String password) {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    return driverCreator.create(uri, new String[]{username, password});
  }


  interface ISession extends IVisitByScript, AutoCloseable {

    /**
     * 执行查询, 返回结果
     */
    IResult query(String q);


    /**
     * 执行查询, 绑定查询参数, 返回结果;
     * 如果驱动不支持该方法则抛出异常.
     */
    IResult query(String q, Map<String, Object> parameters);


    /**
     * 开启事务, 如果驱动不支持该方法则抛出异常;
     * 重复调用该方法会抛出异常.
     */
    void beginTransaction();


    /**
     * 递交事务, 必须首先打开事务
     */
    void commit();


    /**
     * 回滚事务, 必须首先打开事务
     */
    void rollback();
  }


  interface IResult extends IVisitByScript, AutoCloseable {
    /**
     * 如果有更多结果要返回 则返回true
     */
    boolean hasNext();


    /**
     * 返回一条查询结果记录
     */
    Object next();
  }


  public class Neo4jSession implements ISession {

    private Session sess;
    private Transaction tr;


    private Neo4jSession(Session s) {
      this.sess = s;
    }


    @Override
    public IResult query(String q) {
      return query(new Query(q));
    }


    @Override
    public IResult query(String q, Map<String, Object> parameters) {
      return query(new Query(q, parameters));
    }


    private IResult query(Query q) {
      Result r;
      if (tr != null) {
        r = tr.run(q);
      } else {
        r = sess.run(q);
      }
      return new Neo4jResult(r);
    }


    @Override
    public void beginTransaction() {
      if (tr != null) {
        throw new XBosonException("Transaction has been opened");
      }
      tr = sess.beginTransaction();
    }


    @Override
    public void commit() {
      if (tr == null) {
        throw new XBosonException("Transaction not opened");
      }
      tr.commit();
    }


    @Override
    public void rollback() {
      if (tr == null) {
        throw new XBosonException("Transaction not opened");
      }
      tr.rollback();
    }


    @Override
    public void close() throws Exception {
      if (tr != null) {
        tr.close();
      }
      if (sess != null) {
        sess.close();
        sess = null;
      }
    }
  }


  public class Neo4jResult implements IResult {

    private Result r;


    private Neo4jResult(Result r) {
      this.r = r;
    }


    @Override
    public boolean hasNext() {
      return r.hasNext();
    }


    @Override
    public Object next() {
      return value(r.next());
    }


    @Override
    public void close() throws Exception {
    }


    private Object value(Record rc) {
      try {
        Map map = new HashMap(rc.size());
        for (Pair<String, Value> p : rc.fields()) {
          map.put(p.key(), value(p.value()));
        }
        return map;

      } catch(RuntimeException e) {
        // 类型转换错误可能抛出这个异常, 方便调试
        e.printStackTrace();
        throw e;
      }
    }


    private Object value(Value v) {
      if (v.isNull()) return null;
      if (v.isFalse()) return false;
      if (v.isTrue()) return true;
      Object r = v.asObject();

      if (r instanceof Map) {
        return value((Map<String, Value>) r);
      }
      if (r instanceof List) {
        return value((List) r);
      }
      if (r instanceof Node) {
        return value((Node) r);
      }
      if (r instanceof Path) {
        return value((Path) r);
      }
      if (r instanceof Point) {
        return value((Point) r);
      }
      if (r instanceof Relationship) {
        return value((Relationship) r);
      }
      return r;
    }


    private Object value(Map<String, Value> m) {
      Map<String, Object> o = new HashMap<>(m.size());
      for (Map.Entry<String, Value> en : m.entrySet()) {
        o.put(en.getKey(), value(en.getValue()));
      }
      return o;
    }


    private Object value(List<?> l) {
      List<Object> o = new ArrayList<>(l.size());
      for (Object i : l) {
        if (i instanceof Value) {
          o.add(value((Value) i));
        } else {
          o.add(i);
        }
      }
      return o;
    }


    private Object value(Node n) {
      List<Object> ls = new ArrayList<>();
      Map<String, Object> o = new HashMap<>();
      o.put(NODE_TYPE_FIELD, "node");
      o.put("label", ls);
      o.put("id", n.id());
      o.put("propertie", propertie(n));

      for (String label : n.labels()) {
        ls.add(label);
      }
      return o;
    }


    private Object value(Path p) {
      List<Object> node = new ArrayList<>();
      List<Object> rel = new ArrayList<>();
      Map<String, Object> o = new HashMap<>(4);
      o.put(NODE_TYPE_FIELD, "path");
      o.put("node", node);
      o.put("relationship", rel);

      for (Relationship i : p.relationships()) {
        rel.add(value(i));
      }
      for (Node i : p.nodes()) {
        node.add(value(i));
      }
      return o;
    }


    private Object value(Point p) {
      Map<String, Object> o = new HashMap<>(4);
      o.put(NODE_TYPE_FIELD, "point");
      o.put("x", p.x());
      o.put("y", p.y());
      o.put("z", p.z());
      o.put("srid", p.srid());
      return o;
    }


    private Object value(Relationship r) {
      Map<String, Object> o = new HashMap<>(4);
      o.put(NODE_TYPE_FIELD, "rel");
      o.put("start", r.startNodeId());
      o.put("end", r.endNodeId());
      o.put("type", r.type());
      o.put("id", r.id());
      o.put("propertie", propertie(r));
      return o;
    }


    private Object propertie(MapAccessor n) {
      Map<String, Object> prop = new HashMap<>(n.size());

      for (String k : n.keys()) {
        prop.put(k, value(n.get(k)));
      }
      return prop;
    }
  }
}
