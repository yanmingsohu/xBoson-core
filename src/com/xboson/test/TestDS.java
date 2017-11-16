////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-15 下午6:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestDS.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.db.ConnectConfig;

import java.sql.Connection;


/**
 * 用来验证原 DS 相关的业务表
 */
public class TestDS extends TestDBMS {

  private ConnectConfig cc;


  public void test() throws Throwable {
    init_db();
    connect_config();
    tables();
  }


  public void tables() throws Throwable {
    sub("用户列表, 用户名和密码");
    query(cc, "select * from sys_userinfo limit 3");

    sub("角色表");
    query(cc, "select * from sys_role limit 3");

    sub("API 内容表");
    query(cc, "select * from sys_api_content limit 3");
  }


  /**
   * 连接的是上海阿里的数据库, 有效期到 18 年
   */
  public void connect_config() throws Throwable {
    sub("Init connect config");

    cc = new ConnectConfig();
    cc.setDbname("mysql");
    cc.setHost("106.14.26.150");
    cc.setPort("33066");
    cc.setDatabase("a297dfacd7a84eab9656675f61750078");
    cc.setUsername("connuser");
    cc.setPassword("dalianzhirong321_A");

    try (Connection conn = db.open(cc)) {
      conn.createStatement().execute("select 1");
    }
  }

  public static void main(String[] s) {
    new TestDS();
  }
}