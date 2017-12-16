////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-7 下午4:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SeImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.ApiEncryption;
import com.xboson.app.ApiPath;
import com.xboson.app.ApiTypes;
import com.xboson.app.AppContext;
import com.xboson.app.fix.SourceFix;
import com.xboson.been.CallData;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDriver;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.event.OnFileChangeHandle;
import com.xboson.util.IConstant;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * 有部分扩展函数原平台没有.
 */
public class SeImpl extends RuntimeUnitImpl implements AutoCloseable {

  private ConnectConfig __sysdb;
  private Connection __conn;
  private RedisImpl redis;
  private QueryImpl query;
  private SysImpl sys;
  private String orgid;


  public SeImpl(CallData cd, SysImpl sys, String currentOrg) {
    super(cd);
    this.redis = new RedisImpl(IApiConstant._R_KEY_PREFIX_);
    this.query = QueryFactory.create(() -> getConnection(), this);
    this.sys   = sys;
    this.orgid = currentOrg;
  }


  private Connection getConnection() throws Exception {
    if (__conn == null) {
      __conn = DbmsFactory.me().open(getConfig());
    }
    return __conn;
  }


  public ConnectConfig getConfig() {
    if (__sysdb == null) {
      __sysdb = SysConfig.me().readConfig().db;
    }
    return __sysdb;
  }


  public String encodePlatformPassword(String uid, String date, String ps) {
    String md5ps = Password.md5lowstr(ps);
    return Password.v1(uid, md5ps, date);
  }


  public void setCache(String region, String key, Object val, int exp) {
    String str = jsonStringify(val);
    redis.set(region, key, str, exp);
  }


  public Object getCache(String region, String key) {
    String s = redis.get(region, key);
    return jsonParse(s);
  }


  public Object delCache(String region) {
    redis.del(region);
    return region;
  }


  public Object delCache(String region, String key) {
    redis.del(region, key);
    return key;
  }


  public Object delAllCache(String region, String[] keys) throws IOException {
    return redis.delAll(region, keys);
  }


  public Object cacheKeys(String region, String pattern) {
    return redis.keys(createJSList(), region, pattern);
  }


  public String dbType() {
    return dbType(getConfig().getDbid());
  }


  public String dbType(int t) {
    if (t < 10) return "0" + t;
    return String.valueOf(t);
  }


  public boolean isPlatformOrg(String id) {
    return IConstant.SYS_ORG.equalsIgnoreCase(id);
  }


  public boolean isPlatformOrg() {
    return isPlatformOrg(AppContext.me().originalOrg());
  }


  public Object localDb() {
    ConnectConfig db = getConfig();
    IDriver drv = DbmsFactory.me().getDriver(db);
    String url = drv.getUrl(db);

    ScriptObjectMirror info = createJSObject();
    info.setMember("url",       url);
    info.setMember("user",      db.getUsername());
    info.setMember("password",  db.getPassword());
    info.setMember("dbtype",    dbType(db.getDbid()));
    info.setMember("owner",     orgid);
    return unwrap(info);
  }


  public Object query(String sql, String[] param) throws Exception {
    return query(sql, param, "result", false);
  }


  public Object query(String sql, String[] param, String key) throws Exception {
    return query(sql, param, key, false);
  }


  public Object query(String sql, String[] param, String save_to, boolean sw)
          throws Exception {
    if (sw)
      throw new UnsupportedOperationException("不支持替换 schema");

    ScriptObjectMirror arr = createJSList();
    sys.bindResult(save_to, arr);
    return query.query(arr, sql, param);
  }


  public boolean isAuthorizedBizModel(String modelcd) {
    return redis.getRoleInfo(ResourceRoleTypes.MODEL, modelcd) != null;
  }


  public boolean isAuthorizedUI(String pageid) {
    return redis.getRoleInfo(ResourceRoleTypes.PAGE, pageid) != null;
  }


  /**
   * 日志系统已经替换, 不实现
   */
  public void logTopic(String org, String dataset, String tableName, String field) {
    throw new UnsupportedOperationException("Log system not use zookeeper");
  }


  /**
   * 没有被调用过, 不实现
   */
  public void reloadProperties() {
    throw new UnsupportedOperationException("reloadProperties");
  }


  @Override
  public void close() throws Exception {
    if (__conn != null) {
      __conn.close();
      __conn = null;
    }
  }


  /**
   * 返回解密后的脚本, 去掉了前后 "<%..%>" 符号.
   * [原平台无该函数]
   */
  public String decodeApiScript(String code) {
    byte[] c = ApiEncryption.decryptApi(code);
    if (SourceFix.isDrag(c)) {
      int end = c.length - 3;
      while (Character.isWhitespace(c[end]))
        --end;

      c = Arrays.copyOfRange(c, 2, end+1);
    }
    return new String(c, IConstant.CHARSET);
  }


  /**
   * 返回加密后的脚本, 将 "<%..%>" 重新加上.
   * [原平台无该函数]
   */
  public String encodeApiScript(String code) {
    byte[] c = code.getBytes(IConstant.CHARSET);
    if (! SourceFix.isDrag(c)) {
      code = "<%" + code + "%>";
    }
    return ApiEncryption.encryptApi(code);
  }


  /**
   * api 内容被修改, 测试环境脚本将被重新编译.
   * [原平台无该函数]
   */
  public boolean sendApiChange(String content_id) throws SQLException {
    try (SqlResult sr = SqlReader.query(
            "api_info.sql", getConfig(), content_id);
         ResultSet rs = sr.getResult() )
    {
      if (!rs.next()) {
        return false;
      }

      String file_path = ApiPath.getPathOrgFromContext(
              rs.getString("app"),
              rs.getString("mod"),
              rs.getString("api"));

      String event = ApiPath.getEventPath(ApiTypes.Development, file_path);
      OnFileChangeHandle.sendChange(event);
    }
    return true;
  }


  /**
   * api 状态修改, 当状态为 '发布' 则生产环境的脚本将重新编译.
   * [原平台无该函数]
   */
  public boolean sendApiPublish(String app, String mod, String api) {
    String file_path = ApiPath.getPathOrgFromContext(app, mod, api);
    String event = ApiPath.getEventPath(ApiTypes.Production, file_path);
    OnFileChangeHandle.sendChange(event);
    return true;
  }

}
