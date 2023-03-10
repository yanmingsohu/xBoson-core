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
// 文件创建日期: 17-12-7 下午4:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SeImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.*;
import com.xboson.app.fix.SourceFix;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.auth.impl.ResourceRoleTypes;
import com.xboson.been.AppToken;
import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDriver;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.event.EventLoop;
import com.xboson.event.OnFileChangeHandle;
import com.xboson.j2ee.ui.TemplateEngine;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Arrays;


/**
 * 有部分扩展函数原平台没有. <br/>
 * 该对象的实例只在 '平台机构' 中可用.
 */
public class SeImpl extends RuntimeUnitImpl implements AutoCloseable {

  private final static String HIDE_PASS = "********";

  private ConnectConfig __sysdb;
  private RedisImpl redis;
  private QueryImpl query;
  private SysImpl sys;
  private String orgid;
  private ConnectionState state;


  public SeImpl(CallData cd, SysImpl sys, XjOrg currentOrg, ConnectionState cs) {
    super(cd);
    this.redis = new RedisImpl(IApiConstant._R_KEY_PREFIX_);
    this.query = new QueryImpl(cs, this);
    this.sys   = sys;
    this.orgid = currentOrg.id();
    this.state = cs;
  }


  ConnectConfig getSysOrgConfig() {
    if (__sysdb == null) {
      __sysdb = SysConfig.me().readConfig().db;
    }
    return __sysdb;
  }


  /**
   * 计算最终用户密码, 该方法隐藏文档
   * <br/><code>
   * var ps = se.encodePlatformPassword("jym", "2017-01-11 10:47:00", "ps012345");
   * </code>
   *
   * @param uid 用户名字符串
   * @param date 最后修改时间字符串, 用于加密密码的盐
   * @param ps 密码明文
   * @return 返回加密后的密码字符串
   */
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


  public int cacheKeys(String region, String pattern, ScriptObjectMirror callback) {
    if (! callback.isFunction()) {
      throw new XBosonException.BadParameter("callback",
              "must be Function(index, key)");
    }
    return redis.keys(region, pattern, (i, k)->{
      callback.call(null, i, k);
    });
  }


  public String dbType() {
    return state.dbType();
  }


  public String dbType(int t) {
    return ConnectionState.dbType(t);
  }


  public boolean isPlatformOrg(String id) {
    return IConstant.SYS_ORG.equalsIgnoreCase(id);
  }


  public boolean isPlatformOrg() {
    return isPlatformOrg(AppContext.me().originalOrg());
  }


  public Object localDb() {
    ConnectConfig db = getSysOrgConfig();
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
    sys.addRetData(arr, save_to);
    return query.query(arr, sql, param);
  }


  public QueryImpl.ResultReader queryStream(String sql, Object[] param)
          throws Exception {
    return query.queryStream(sql, param);
  }


  public boolean isAuthorizedBizModel(String modelcd) {
    return redis.getRoleInfo(ResourceRoleTypes.MODEL, modelcd) != null;
  }


  public Object isAuthorizedUI(String pageid) {
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


  /**
   * 重新加载指定用户的角色缓存信息, 相当于重新登录.
   * 省去客户在浏览器上重登录. [空实现, 什么都不做]
   */
  public void reloadUserRole(Object[] userid) {
  }


  /**
   * @see #reloadUserRole(Object[])
   */
  public void reloadUserGroupRole(Object[] grpid) {
  }


  /**
   * @see #reloadUserRole(Object[])
   */
  public void reloadUserInfo(Object[] userid) {
  }


  /**
   * @see #reloadUserRole(Object[])
   */
  public void reloadClientRole(Object[] clientid) {
  }


  @Override
  public void close() throws Exception {
    state.close();
  }


  /**
   * 返回解密后的脚本, 去掉了前后 "<%..%>" 符号.
   * [原平台无该函数, 该方法隐藏文档]
   */
  public String decodeApiScript(String code) {
    checkApiAuth();
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
   * [原平台无该函数, 该方法隐藏文档]
   */
  public String encodeApiScript(String code) {
    checkApiAuth();
    byte[] c = code.getBytes(IConstant.CHARSET);
    if (! SourceFix.isDrag(c)) {
      code = "<%" + code + "%>";
    }
    return ApiEncryption.encryptApi(code);
  }


  /**
   * 返回解密后的脚本, 去掉了前后 "<%..%>" 符号.
   * [原平台无该函数, 该方法隐藏文档]
   */
  public String decryptApi2(String code, int zip) throws Exception {
    if (zip == 0) return decodeApiScript(code);
    checkApiAuth();
    return new String(ApiEncryption.me.decryptApi2(code, zip), IConstant.CHARSET);
  }


  /**
   * 返回加密后的脚本
   * [原平台无该函数, 该方法隐藏文档]
   */
  public String encodeApi2(String code, int zip) throws Exception {
    if (zip == 0) return encodeApiScript(code);
    checkApiAuth();
    return ApiEncryption.me.encryptApi2(code, zip);
  }


  private void checkApiAuth() {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class,
            ()-> "api.ide.code.modify.functions()");
  }


  /**
   * 生成源代码的 zip 值, 该值在 DB.sys_api_content.zip 列的有效范围内
   */
  public int genZip(String code) {
    int i = 0;
    for (int a = code.length()-1; a>=0; --a) {
      i += code.charAt(a);
    }
    return i % 255 + 1;
  }


  /**
   * api 内容被修改, 测试环境脚本将被重新编译.
   * [原平台无该函数]
   */
  public boolean sendApiChange(String content_id) throws Exception {
    Object[] bind = { AppContext.me().originalOrg() };
    try (SqlResult sr = SqlReader.query(
            "api_info.sql", bind, getSysOrgConfig(), content_id);
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


  /**
   * 当 html 模板标签库被修改后, 调用该方法使模板引擎重新加载标签库.
   * [原平台无该函数]
   */
  public void sendUITemplateReloadTag() {
    TemplateEngine.reloadAllTags();
  }


  /**
   * 当 html 文件修改后, 调用该方法使模板引擎重新加载指定的文件.
   * @param pagePath
   * @param type 'addfile', 'change', 'removefile'
   */
  public void sendUIFileReload(String pagePath, String type) {
    TemplateEngine.fileChange(pagePath, type);
  }


  public void sendUIFileReload(String pagePath) {
    sendUIFileReload(pagePath, "change");
  }


  /**
   * 重新读取已经发布的应用程序列表
   */
  public void sendAppReleased() {
    EventLoop.me().add(() -> AppContext.me().rebuildAppOrgMapping());
  }


  /**
   * 从缓存中删除 Token, 下次访问令牌将强制更新令牌.
   * [原平台无该函数]
   */
  public void updateToken(String token) {
    AppToken at = new AppToken();
    at.token = token;
    RedisMesmerizer.me().remove(at);
  }
}
