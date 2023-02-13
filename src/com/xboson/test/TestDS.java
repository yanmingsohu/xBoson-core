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
// 文件创建日期: 17-11-15 下午6:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestDS.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.Config;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.script.lib.Uuid;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;

import java.sql.Connection;


/**
 * 用来验证原 DS 相关的业务表
 */
public class TestDS extends TestDBMS {

  private ConnectConfig cc;


  public void test() throws Throwable {
    cc = connect_config();
    init_db();
    connect_config();
    tables();
    create_root();
  }


  public void tables() throws Throwable {
    sub("用户列表, 用户名和密码");
    query(cc, "select * from sys_userinfo limit 3");

    sub("角色表");
    query(cc, "select * from sys_role limit 3");

    sub("API 内容表");
    query(cc, "select * from sys_api_content limit 3");
  }


  public void create_root() throws Throwable {
    Config cf = SysConfig.me().readConfig();
    Uuid uuid = new Uuid();
    String pid = cf.rootPid; // uuid.ds();

    // !!! 创建超级用户可用, 需要迁移到正式代码中
    //    query("create_user.sql", pid,
    //            cf.rootUserName, cf.rootPassword, Password.salt);
    //    query("join_all_group.sql", pid);
    //    query("join_all_org.sql", pid);

    String orglist = SqlReader.read("user0003");
    TestDBMS.query(cc, orglist, pid);
  }


  private void query(String sql, Object ...parms) {
    try (SqlResult r1 = SqlReader.query(sql, cc, parms))
    {
      msg("Success", sql);
    } catch (Exception e) {
      fail(sql, e);
      e.printStackTrace();
    }
  }


  /**
   * 连接的是上海阿里的数据库, 有效期到 2018 年
   */
  public static ConnectConfig connect_config() throws Throwable {
    sub("Init connect config");
    ConnectConfig cc;

    cc = new ConnectConfig();
    cc.setDbname("mysql");
    cc.setHost("106.14.26.150");
    cc.setPort("33066");
    cc.setDatabase("a297dfacd7a84eab9656675f61750078");
    cc.setUsername("connuser");
    cc.setPassword("dalianzhirong321_A");

    try (Connection conn = DbmsFactory.me().open(cc)) {
      conn.createStatement().execute("Select 1");
    }

    return cc;
  }

  public static void main(String[] s) {
    new TestDS();
  }
}
