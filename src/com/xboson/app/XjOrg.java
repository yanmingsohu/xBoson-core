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
// 文件创建日期: 17-11-22 下午1:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/Org.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.lib.QueryFactory;
import com.xboson.app.reader.AbsReadScript;
import com.xboson.been.SysPlDrmDs001;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDict;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.Tool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 为机构创建一个有限权限的数据库用户, 并将连接配置保存在表中,
 * 使用这个有限权限的连接开发 api.
 */
public class XjOrg extends XjPool<XjApp> implements IDict, IConstant {

  /** sys_pl_drm_ds001.flg 必须是 9 才可以打开数据库. */
  static final String XBOSON_TYPE = FLG_SYS +"";

  private String orgid;
  private String name;
  private ConnectConfig orgdb;
  private ConnectConfig rootdb;
  private boolean isSysOrg;
  private AbsReadScript script_reader;


  /**
   * 创建机构
   * @param dbcc root 权限数据库连接配置
   * @param orgid 当前机构 id
   * @param script_reader 脚本读取器
   */
  XjOrg(ConnectConfig dbcc, String orgid, AbsReadScript script_reader) {
    this.orgid = orgid;
    this.rootdb = dbcc;
    this.isSysOrg = SYS_ORG.equalsIgnoreCase(orgid);
    this.script_reader = script_reader;

    try (Connection conn = DbmsFactory.me().open(rootdb)) {
      check_org(conn);
      get_org_db_config(conn);
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
    log.debug("Org success", orgid);
  }


  /**
   * 是系统机构返回 true
   */
  public boolean isSysORG() {
    return isSysOrg;
  }


  /**
   * 创建或获取缓存的 app
   * @param id app-id
   * @return app
   */
  public XjApp getApp(String id) {
    return super.getWithCreate(id);
  }


  private void get_org_db_config(Connection conn) throws SQLException {
    SysPlDrmDs001 dbconf = new SysPlDrmDs001();

    SqlResult res = SqlResult.query(
            conn, SqlReader.read("open_org_db.sql"), orgid);
    int row = res.oneRowTo(dbconf);

    if (row == 0) {
      create_org_db_user(conn);
    }
    else if (! XBOSON_TYPE.equals(dbconf.flg)) {
      throw new XBosonException("系统异常: 数据库配置不是 flg==9 无法打开机构数据库");
    }
    else {
      check_mysql_user(conn, dbconf);
      set_org_db(dbconf);
    }
  }


  /**
   * 当迁移数据库后, 平台中保存的 mysql 帐号可能并不存在, 该方法检测 mysql 帐号
   * 并在必要时创建 mysql 帐号.
   */
  private void check_mysql_user(Connection conn, SysPlDrmDs001 dbconf)
          throws SQLException {
    SqlResult res = SqlResult.query(conn,
            SqlReader.read("mysql_user_exist.sql"), dbconf.user_name);

    ResultSet rs = res.getResult();
    if (rs.next() && rs.getInt(1) > 0) {
      return;
    }
    do_grant(conn, dbconf.user_name, dbconf.pass);
    log.debug("Mysql User not exists, Create:",
            dbconf.user_name, "Org:", dbconf.owner);
  }


  private void set_org_db(SysPlDrmDs001 dbconf) {
    orgdb = new ConnectConfig();
    orgdb.setDbname(DbmsFactory.me().findDriver(dbconf.dbtype).name());
    orgdb.setHost(dbconf.dhost);
    orgdb.setPort(dbconf.dport.toString());
    orgdb.setDatabase(dbconf.en);
    orgdb.setUsername(dbconf.user_name);
    orgdb.setPassword(dbconf.pass);
    orgdb.flg = Integer.parseInt(dbconf.flg);
  }


  /**
   * 无法通过数据绑定, 只能拼 sql
   */
  private void do_grant(Connection conn, String un, String ps)
          throws SQLException {
    String sql =
            "Grant\n" +
            "  ALTER, ALTER ROUTINE, CREATE, CREATE ROUTINE,\n" +
            "  EXECUTE, REFERENCES, TRIGGER, LOCK TABLES,\n" +
            "  DROP, DELETE, INDEX, INSERT, SELECT, UPDATE\n" +
            "ON\n" +
            "  `" + orgid + "`.*\n" +
            "TO\n" +
            "  '" + un + "'@'%%' identified by '" + ps + "'";

    Statement s = conn.createStatement();
    s.executeUpdate(sql);
    s.executeUpdate("Flush Privileges");
    log.debug("Create DB User:", un);
  }


  private void create_org_db_user(Connection conn) throws SQLException {
    create_org_db_user(conn,
            Tool.randomString2(30),
            Tool.randomString2(30));
  }


  private void create_org_db_user(Connection conn, String un, String ps)
          throws SQLException {
    do_grant(conn, un, ps);

    Object[] parm = new Object[] {
            Tool.uuid.ds(), orgid, rootdb.getDbid(), orgid,
            rootdb.getHost(), rootdb.getPort(), un, ps };

    String sql = SqlReader.read("create_db_conf.sql");
    SqlResult res = SqlResult.query(conn, sql, parm);
    if (!res.isUpdate()) {
      throw new XBosonException("保存 DB 连接配置到平台数据源失败");
    }

    orgdb = rootdb.clone();
    orgdb.setDatabase(orgid);
    orgdb.setUsername(un);
    orgdb.setPassword(ps);

    log.debug("Save MYSQL user to 'sys_pl_drm_ds001'",
            "Org:", orgid, "User:", un);
  }


  private void check_org(Connection conn) throws SQLException {
    String sql = SqlReader.read("open_org.sql");

    SqlResult res = SqlResult.query(conn, sql, orgid);
    ResultSet rs = res.getResult();
    if (rs.next()) {
      if (! ZR001_ENABLE.equals(rs.getString("status")) ) {
        throw new XBosonException("机构已经禁用");
      }
      name = rs.getString("de0810013j");
    } else {
      throw new XBosonException("找不到机构: " + orgid);
    }
  }


  @Override
  protected XjApp createItem(String id) {
    return new XjApp(this, id);
  }


  /**
   * 使用机构权限的数据库连接来查询
   */
  public SqlResult query(String sqlfile, Object ...parm) {
    if (isSysOrg) return queryRoot(sqlfile, parm);
    return SqlReader.query(sqlfile, orgdb, parm);
  }


  /**
   * 使用 root 权限的数据连接来查询
   */
  public SqlResult queryRoot(String sqlfile, Object ...parm) {
    return SqlReader.query(sqlfile, rootdb, parm);
  }


  public String id() {
    return orgid;
  }


  ConnectConfig getOrgDb() {
    if (isSysOrg) return rootdb;
    return orgdb;
  }


  public AbsReadScript getScriptReader() {
    return script_reader;
  }


  @Override
  public String logName() {
    return "sc-core-org";
  }
}
