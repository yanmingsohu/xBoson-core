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
// 文件创建日期: 17-11-23 上午11:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SqlImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.IAResource;
import com.xboson.been.CallData;
import com.xboson.been.Page;
import com.xboson.been.XBosonException;
import com.xboson.db.*;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.sql.*;


/**
 * 每次请求一个实例
 */
public class SqlImpl extends RuntimeUnitImpl implements AutoCloseable, IAResource {

  private final static String NO_CONNECTION = "not connection";
  private final static String NO_POOL_CONNECTION = "connection without pool";

  private final ConnectConfig orgdb;
  private QueryImpl query_impl;
  private SysImpl sys;
  private ConnectionState state;


  public SqlImpl(CallData cd, ConnectConfig orgdb, ConnectionState cs) throws SQLException {
    super(cd);
    this.orgdb = orgdb;
    this.state = cs;

    state.setQueryImplEventListener((qi)-> this.query_impl = qi);
    state.reset(orgdb, null, dbType(orgdb.getDbid()));
  }


  /**
   * sql 将查询结果绑定在 sys 上.
   */
  public void _setSysRef(SysImpl sys) {
    this.sys = sys;
  }


  public int query(String sql, Object[] param) throws Exception {
    return query(sql, param, "result");
  }


  public int query(String sql, Object[] param, String save_to)
          throws Exception {
    ScriptObjectMirror arr = createJSList();
    sys.addRetData(arr, save_to);
    return query_impl.query(arr, sql, param);
  }


  /**
   * 带有分页的查询
   *
   * @param sql 带有 ? 绑定参数的 sql 文
   * @param param sql 文中对应的参数列表
   * @param pageNum 页号, 从 1 开始
   * @param pageSize 查询返回行数
   * @param save_to 结果集保存到 save_to 的属性名, 总行数保存在 save_to+'_count' 中.
   * @param totalCount 提供该值可以省去计算总行数
   * @return 总行数
   */
  public int queryPaging(String sql, Object[] param, int pageNum, int pageSize,
                         String save_to, int totalCount) throws Exception {
    ScriptObjectMirror arr = createJSList();
    sys.addRetData(arr, save_to);
    Page page = new Page(pageNum, pageSize, totalCount);
    int total = query_impl.queryPaging(arr, sql, param, page, state.config());
    sys.addRetData(total, save_to + _COUNT_SUFFIX_);
    sys.bindResult(_COUNT_NAME_, total);
    return total;
  }


  public int queryPaging(String sql, Object[] param, int pageNum, int pageSize,
                         String save_to) throws Exception {
    return queryPaging(sql, param, pageNum, pageSize,
            save_to, Page.PAGE_DEFAULT_COUNT);
  }


  public int queryPaging(String sql, Object[] param, int pageNum, int pageSize)
                         throws Exception {
    return queryPaging(sql, param, pageNum, pageSize,
            "result", Page.PAGE_DEFAULT_COUNT);
  }


  public QueryImpl.ResultReader queryStream(String sql, Object[] param)
          throws Exception {
    return query_impl.queryStream(sql, param);
  }


  public QueryImpl.ResultReader queryStream(String sql)
          throws Exception {
    return query_impl.queryStream(sql, null);
  }


  public int update(String sql, Object[] param, String manualCommit) throws Exception {
    return update(sql, param, isManualCommit(manualCommit));
  }


  public int update(String sql, Object[] param, int manualCommit) throws Exception {
    return update(sql, param, manualCommit >= 1);
  }


  public int update(String sql, Object[] param) throws Exception {
    return update(sql, param, (Boolean)null);
  }


  public int update(String sql) throws Exception {
    return update(sql, null, (Boolean)null);
  }


  public int update(String sql, Object[] param, Boolean manualCommit)
          throws Exception {
    Connection conn = state.open();
    if (manualCommit != null) {
      conn.setAutoCommit(!manualCommit);
    }

    sql = query_impl.replaceSql(sql);

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      if (param != null) {
        for (int i = 1; i <= param.length; ++i) {
          Object p = param[i - 1];
          ps.setObject(i, getSafeObjectForQuery(p));
        }
      }
      return ps.executeUpdate();
    }
  }


  public boolean isManualCommit(String flag) {
    return flag.equals("1") || flag.equalsIgnoreCase("true");
  }


  public int updateBatch(String sql, Object _param_grp) throws Exception {
    return updateBatch(sql, _param_grp, (Boolean)null);
  }


  public int updateBatch(String sql, Object _param_grp, String commitFlag)
          throws Exception {
    return updateBatch(sql, _param_grp, isManualCommit(commitFlag));
  }


  public int updateBatch(String sql, Object _param_grp, Boolean manualCommit)
          throws Exception {
    ScriptObjectMirror param_grp = wrap(_param_grp);
    Connection conn = state.open();
    if (manualCommit != null) {
      conn.setAutoCommit(! manualCommit);
    }

    sql = query_impl.replaceSql(sql);

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      int total = 0;
      final int len = param_grp.size();

      for (int g = 0; g < len; ++g) {
        ScriptObjectMirror param = wrap(param_grp.getSlot(g));
        final int param_len = param.size();

        for (int i = 1; i <= param_len; ++i) {
          Object p = param.getSlot(i-1);
          ps.setObject(i, getSafeObjectForQuery(p));
        }
        total += ps.executeUpdate();
      }
      return total;
    }
  }


  public Object metaData(String sql) throws Exception {
    sql = query_impl.replaceSql(sql);

    try (PreparedStatement ps = state.open().prepareStatement(sql)) {
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
  }


  public void commit() throws Exception {
    state.open().commit();
  }


  public void rollback() throws Exception {
    state.open().rollback();
  }


  public void setAutoCommit(boolean b) throws Exception {
    state.open().setAutoCommit(b);
  }


  public String currentDBTimeString() throws Exception {
    IDialect dialect = DbmsFactory.me().getDriver(state.config());
    try (Statement stat = state.open().createStatement()) {
      ResultSet rs = stat.executeQuery(dialect.nowSql());
      if (rs.next()) {
        Timestamp d = rs.getTimestamp(IDialect.NOW_TIME_COLUMN);
        return Tool.formatDate(d);
      }
      return null;
    }
  }


  public void connection() throws Exception {
    state.close();
    state.connect_orgdb();
    state.reset_query_repl(orgdb);
  }


  public void connection(String key) throws Exception {
    //PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    ConnectConfig connsetting = sourceConfig(key, cd.sess.login_user.userid);
    Connection newconn = DbmsFactory.me().open(connsetting);
    try {
      state.close();
      state.reset(connsetting, newconn, dbType(connsetting.getDbid()));
    } catch(Exception e) {
      newconn.close();
    }
  }


  /**
   * 不用连接池保持连接.
   */
  public void connection(String url, String user, String ps) throws Exception {
    //PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    Connection newconn = DriverManager.getConnection(url, user, ps);
    try {
      if (!newconn.isValid(1000)) {
        throw new XBosonException("Cannot connect to url");
      }
      state.close();
      state.reset(null, newconn, NO_POOL_CONNECTION);
    } catch(Exception e) {
      newconn.close();
    }
  }


  /**
   * 不用连接池保持连接.
   */
  public String connection(String dbType, String host, int port, String db,
                         String user, String ps) throws Exception {
    IDriver dri = DbmsFactory.me().findDriver(dbType);
    if (dri == null)
      throw new XBosonException("Cannot find DB driver "+ dbType);

    if (port <= 0) port = dri.port();

    ConnectConfig cc = new ConnectConfig();
    cc.setDbid(dri.id());
    cc.setHost(host);
    cc.setPort(Integer.toString(port));
    cc.setDatabase(db);
    String url = dri.getUrl(cc);
    connection(url, user, ps);
    return url;
  }


  /**
   * 从数据源配置列表中获取一个数据源配置, 会检查用户对数据源的权限, 失败抛出异常.
   * @param sourceID 数据源 id
   * @param userID 用户 id
   * @return 数据库配置
   */
  public static ConnectConfig sourceConfig(String sourceID, String userID)
          throws SQLException {
    if (userID == null) {
      throw new XBosonException("Cannot found USER_ID in request");
    }

    ConnectConfig db =  SysConfig.me().readConfig().db;

    try (SqlResult sr = SqlReader.query(
            "open_db_with_userid", db, sourceID, userID)) {
      ResultSet rs = sr.getResult();
      if (! rs.next()) {
        throw new XBosonException("Cannot connect to DB: " + sourceID);
      }

      ConnectConfig conf = new ConnectConfig();
      conf.setDbid(rs.getInt("dbid"));
      conf.setHost(rs.getString("host"));
      conf.setPort(rs.getString("port"));
      conf.setUsername(rs.getString("username"));
      conf.setPassword(rs.getString("password"));
      conf.setDatabase(rs.getString("database"));
      conf.flg = rs.getInt("flg");
      return conf;
    }
  }


  public String dbType() {
    return state.dbType();
  }


  private String dbType(int id) {
    return ConnectionState.dbType(id);
  }


  public void msAccessConnection(String path, String pwd, String charset) {
    throw new UnsupportedOperationException("msAccessConnection");
  }


  @Override
  public void close() throws Exception {
    state.close();
  }


  @Override
  public String description() {
    return "app.module.sql.switch.org()";
  }
}
