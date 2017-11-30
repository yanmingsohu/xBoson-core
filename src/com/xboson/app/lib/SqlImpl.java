////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SqlImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.AuthFactory;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.OpenSystemDBWithKey;
import com.xboson.been.CallData;
import com.xboson.db.*;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.sql.*;
import java.util.Calendar;


/**
 * 每次请求一个实例
 */
public class SqlImpl extends RuntimeUnitImpl implements AutoCloseable {

  private Connection conn;
  private ConnectConfig orgdb;

  public ScriptObjectMirror result;
  public String _dbType;


  public SqlImpl(CallData cd, ConnectConfig orgdb) throws SQLException {
    super(cd);
    this.orgdb = orgdb;
    connection();
  }


  public int query(String sql, Object[] param) throws SQLException {
    return query(sql, param, "result");
  }


  public int query(String sql, Object[] param, String save_to)
          throws SQLException {
    if (result == null) result = createJSObject();

    PreparedStatement ps = conn.prepareStatement(sql);
    ScriptObjectMirror arr = createJSList();
    result.setMember(save_to, arr);

    for (int i=1; i<=param.length; ++i) {
      Object p = param[i-1];
      ps.setObject(i, p);
    }

    ResultSet rs = ps.executeQuery();
    ResultSetMetaData meta = rs.getMetaData();
    int column = meta.getColumnCount();
    int row_count = 0;
    int arri = arr.size()-1;

    while (rs.next()) {
      ScriptObjectMirror row = createJSObject();
      arr.setSlot(++arri, row);
      ++row_count;

      for (int c = 1; c<=column; ++c) {
        row.setMember(meta.getColumnLabel(c), rs.getObject(c));
      }
    }

    rs.close();
    ps.close();
    return row_count;
  }


  /**
   * 带有分页的查询
   *
   * @param sql 带有 ? 绑定参数的 sql 文
   * @param param sql 文中对应的参数列表
   * @param pageNum 页号, 从 1 开始
   * @param pageSize 查询返回行数
   * @param save_to 结果集保存到 result 的属性名
   * @param totalCount 提供该值可以省去计算总行数
   * @return
   */
  public int queryPaging(String sql, Object[] param, int pageNum, int pageSize,
                         String save_to, int totalCount) {
    return 0;
  }


  public int update(String sql, Object[] param, String commit) throws SQLException {
    boolean is_commit = commit.equals("1") || commit.equalsIgnoreCase("true");
    return update(sql, param, is_commit);
  }


  public int update(String sql, Object[] param) throws SQLException {
    return update(sql, param, true);
  }


  public int update(String sql, Object[] param, boolean commit)
          throws SQLException {
    conn.setAutoCommit(commit);
    PreparedStatement ps = conn.prepareStatement(sql);

    for (int i=1; i<=param.length; ++i) {
      Object p = param[i-1];
      ps.setObject(i, p);
    }

    return ps.executeUpdate();
  }


  public int updateBatch(String sql, Object[] param_grp, boolean commit)
          throws SQLException {
    conn.setAutoCommit(commit);
    PreparedStatement ps = conn.prepareStatement(sql);
    int total = 0;

    for (int g = 0; g<param_grp.length; ++g) {
      ScriptObjectMirror param = wrap(param_grp[g]);
      final int param_len = param.size();

      for (int i = 1; i <= param_len; ++i) {
        Object p = param.getSlot(i-1);
        ps.setObject(i, p);
      }
      total += ps.executeUpdate();
    }

    ps.close();
    return total;
  }


  public Object metaData(String sql) throws SQLException {
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSetMetaData meta = ps.getMetaData();
    int column_count = meta.getColumnCount();
    ScriptObjectMirror attr_list = createJSList(column_count);
    int attr_i = attr_list.size() - 1;

    for (int i=1; i<=column_count; ++i) {
      ScriptObjectMirror attr = createJSObject();
      attr_list.setSlot(++attr_i, attr);

      attr.setMember("ColumnLabel",    meta.getColumnLabel(i));
      attr.setMember("ColumnName",     meta.getColumnName(i));
      attr.setMember("ColumnTypeName", meta.getColumnTypeName(i));
      attr.setMember("Precision",      meta.getPrecision(i));
      attr.setMember("Scale",          meta.getScale(i));
      attr.setMember("TableName",      meta.getTableName(i));
      attr.setMember("SchemaName",     meta.getSchemaName(i));
      attr.setMember("CatalogName",    meta.getCatalogName(i));
    }

    return attr_list;
  }


  public void commit() throws SQLException {
    conn.commit();
  }


  public void rollback() throws SQLException {
    conn.rollback();
  }


  public String currentDBTimeString() throws SQLException {
    IDialect dialect = DbmsFactory.me().getDriver(orgdb);
    Statement stat = conn.createStatement();
    ResultSet rs = stat.executeQuery(dialect.nowSql());
    if (rs.next()) {
      Timestamp d = rs.getTimestamp(1);
      return Tool.formatDate(d);
    }
    return null;
  }


  public void connection() throws SQLException {
    Tool.close(conn);
    this.conn = DbmsFactory.me().open(orgdb);
    IDriver d = DbmsFactory.me().getDriver(orgdb);
    setDBType(d);
  }


  public void connection(String key) throws SQLException {
    ConnectConfig db =  SysConfig.me().readConfig().db;
    String userid = cd.sess.login_user.userid;

    try (SqlResult sr = SqlReader.query(
            "open_db_with_userid", db, key, userid)) {
      ResultSet rs = sr.getResult();
      rs.next();

      ConnectConfig connsetting = new ConnectConfig();
      connsetting.setDbid(rs.getInt("dbid"));
      connsetting.setHost(rs.getString("host"));
      connsetting.setPort(rs.getString("port"));
      connsetting.setUsername(rs.getString("username"));
      connsetting.setPassword(rs.getString("password"));
      connsetting.setDatabase(rs.getString("database"));

      Connection newconn = DbmsFactory.me().open(connsetting);
      Tool.close(conn);
      conn = newconn;
    }
  }


  public void connection(String url, String user, String ps) {
  }


  public String dbType() {
    return _dbType;
  }


  private void setDBType(IDriver d) {
    int id = d.id();
    if (id < 10) {
      _dbType = "0" + id;
    } else {
      _dbType = Integer.toString(id);
    }
  }


  public void msAccessConnection(String path, String pwd, String charset) {
    throw new UnsupportedOperationException("msAccessConnection");
  }


  @Override
  public void close() throws Exception {
    Tool.close(conn);
    conn = null;
  }
}
