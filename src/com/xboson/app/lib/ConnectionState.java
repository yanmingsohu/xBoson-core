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
