////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-4 下午7:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ConnectionState.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDriver;

import java.sql.Connection;


public final class ConnectionState extends RuntimeUnitImpl
        implements QueryImpl.SqlConnect, AutoCloseable {

  interface QueryImplEvent {
    void onChange(QueryImpl i);
  }


  private final ConnectConfig orgdb;
  private ConnectConfig __currdb;
  private Connection __conn;
  private String _dbType;
  private QueryImplEvent qe;


  public ConnectionState(CallData cd, ConnectConfig org) {
    super(cd);
    this.orgdb = org;
  }


  public void setQueryImplEventListener(QueryImplEvent e) {
    this.qe = e;
  }


  /**
   * 返回的对象不要关闭
   */
  @Override
  public Connection open() throws Exception {
    if (__conn == null || __conn.isClosed()) {
      // SqlImpl 创建时没有立即连接数据库, 直到首次执行 sql 查询时, 连接到默认 db.
      connect_orgdb();
    }
    return __conn;
  }


  @Override
  public void close() throws Exception {
    if (__conn != null) {
      __conn.close();
      __conn = null;
    }
  }


  /**
   * 连接到核心机构, 但不发送 QueryImplEvent 消息
   */
  public void connect_orgdb() throws Exception {
    IDriver d = DbmsFactory.me().getDriver(orgdb);
    reset_info(orgdb, DbmsFactory.me().open(orgdb), dbType(d.id()));
  }



  /**
   * 重制连接信息, 重新绑定 sql 替换器
   * 发送 QueryImplEvent 消息
   */
  public void reset(ConnectConfig conf, Connection conn, String type) {
    reset_info(conf, conn, type);
    reset_query_repl(conf);
  }


  /**
   * 发送 QueryImplEvent 消息
   */
  public void reset_query_repl(ConnectConfig conf) {
    if (qe == null) return;

    QueryImpl qi = null;
    if (conf != null) {
      qi = QueryFactory.create(this, this, conf);
    } else {
      qi = new QueryImpl(this, this);
    }
    qe.onChange(qi);
  }


  public void reset_info(ConnectConfig conf, Connection conn, String type) {
    __currdb = conf;
    __conn   = conn;
    _dbType  = type;
  }


  public String dbType() {
    return _dbType;
  }


  public ConnectConfig config() {
    return __currdb;
  }


  public static String dbType(int id) {
    if (id < 10) {
      return "0" + id;
    } else if (id < 100) {
      return Integer.toString(id);
    } else {
      throw new XBosonException("db type id > 100");
    }
  }
}
