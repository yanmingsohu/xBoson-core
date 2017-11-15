////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-14 上午9:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestDBMS.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDriver;
import com.xboson.db.driver.Mysql;

import java.sql.*;


public class TestDBMS extends Test {

  public final static int SHOW_RESULT_LINE = 3;
  private DbmsFactory db;


  public void test() throws Throwable {
    db = DbmsFactory.me();
    db.registeringDefaultDriver();

    mysql();
  }


  public void mysql() throws Throwable {
    sub("Mysql");

    ConnectConfig cc = new ConnectConfig();
    cc.setDbname("mysql");
    cc.setDatabase("eeb");
    cc.setHost("localhost");
    cc.setUsername("root");
    cc.setPassword("root");

    IDriver dr = db.getDriver(cc);
    eq(dr.getClass(), Mysql.class, "mysql driver");

    query(cc, "select * from sys_eeb_detail");
    query(cc, "select * from sys_eeb_run_conf");
    query(cc, "select * from sys_eeb_statistics");
  }


  public void query(ConnectConfig cc, String sql) throws SQLException {
    beginTime();
    Statement stat = null;
    try (Connection conn = db.open(cc)) {
      endTime("Open Connection");
      beginTime();
      stat = conn.createStatement();
      ResultSet set = stat.executeQuery(sql);
      show(set);
      endTime("Query", sql);
    } finally {
      if (stat != null) {
        ok(stat.isClosed(), "Statement closed yet");
      }
    }
  }


  public void show(ResultSet rs) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    StringBuilder out = new StringBuilder("\n");
    final int cc = meta.getColumnCount();
    final String name = "[ TABLE -"+ meta.getTableName(1) +" ]";

    for (int i=1; i<=cc; ++i) {
      out.append(meta.getColumnLabel(i));
      out.append('\t');
    }
    out.append('\n');
    out.append(line);
    sub(out);
    out.setLength(0);

    int showline = SHOW_RESULT_LINE;

    while (rs.next()) {
      for (int c = 1; c <= cc; ++c) {
        out.append(rs.getObject(c));
        out.append('\t');
      }
      msg(out);
      out.setLength(0);

      if (--showline <= 0)
        break;
    }

    if (showline <= 0) {
      sub("... More lines ... ", name, "\n");
    } else {
      sub(name, "\n");
    }
  }


  public static void main(String []a) {
    new TestDBMS();
  }
}
