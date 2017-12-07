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

import com.xboson.been.CallData;
import com.xboson.db.ConnectConfig;
import com.xboson.util.IConstant;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;

import java.io.IOException;


public class SeImpl extends RuntimeUnitImpl {

  private RedisImpl redis;
  private ConnectConfig sysdb;


  public SeImpl(CallData cd) {
    super(cd);
    redis = new RedisImpl("/sys/");
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
    if (sysdb == null) {
      sysdb = SysConfig.me().readConfig().db;
    }
    int t = sysdb.getDbid();
    if (t < 10) return "0" + t;
    return String.valueOf(t);
  }


  public boolean isPlatformOrg(String id) {
    return IConstant.SYS_ORG.equalsIgnoreCase(id);
  }


  public Object localDb() {
    return null;
  }


  public Object query(String sql, String[] param, String key) {
    return query(sql, param, key, false);
  }


  public Object query(String sql, String[] param, String key, boolean sw) {
    throw new UnsupportedOperationException();
  }
}
