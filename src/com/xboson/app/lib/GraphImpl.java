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

import java.util.Map;


public class GraphImpl extends RuntimeUnitImpl implements IVisitByScript, IAResource {


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


  interface IResult extends IVisitByScript {
    /**
     * 如果有更多结果要返回 则返回true
     */
    boolean hasNext();


    /**
     * 返回一条查询结果记录
     */
    Map next();

  }


  private class Neo4jSession implements ISession {

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
      sess.close();
    }
  }


  private class Neo4jResult implements IResult {

    private Result r;


    private Neo4jResult(Result r) {
      this.r = r;
    }


    @Override
    public boolean hasNext() {
      return r.hasNext();
    }


    @Override
    public Map next() {
      return r.next().asMap();
    }
  }
}
