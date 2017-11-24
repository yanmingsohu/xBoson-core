////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/SysImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.db.ConnectConfig;
import com.xboson.db.SqlCachedResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.script.lib.Uuid;
import com.xboson.util.ChineseInital;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.converter.ScriptObjectMirrorConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeJSON;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 每次请求一个实例
 */
public class SysImpl extends RuntimeImpl {

  /**
   * 公共属性
   */
  public final RequestImpl request;
  public final RequestParametersImpl requestParameterMap;

  private ConnectConfig orgdb;
  private Map<String, Object> retData;
  private static long id = 0;

  static {
    id = (long)(Uuid.HALF * Math.random());
  }


  public SysImpl(CallData cd, ConnectConfig orgdb) {
    super(cd);
    this.orgdb = orgdb;
    this.request = new RequestImpl(cd);
    this.retData = new HashMap<>();
    this.requestParameterMap = new RequestParametersImpl(cd);
  }


  public void addRetData(Object o) {
    addRetData(o, "result");
  }


  public void addRetData(Object o, String key) {
    if (o instanceof ScriptObjectMirror) {
      retData.put(key, new ScriptObjectMirrorConverter.Warp(o));
    } else {
      retData.put(key, o);
    }
  }


  public void setRetData(String code, String msg, String ...parm)
          throws IOException {
    int c = Integer.parseInt(code);
    setRetData(c, msg, parm);
  }


  public void setRetData(int code, String msg, String ...parm)
          throws IOException {
    cd.xres.setCode(code);
    cd.xres.setMessage(msg);
    for (int i=0; i<parm.length; ++i) {
      String name = parm[i];
      cd.xres.bindResponse(name, retData.get(name));
    }
    cd.xres.response();
  }


  public String getUserPID() {
    return cd.sess.login_user.pid;
  }


  public Object getUserPID(String ...users) throws Exception {
    Map<String, Object> ret = new HashMap<>(users.length);

    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_id_to_pid.sql");

      for (int i=0; i<users.length; ++i) {
        List<Map<String, Object>> rows = scr.query(sql, users[i]);
        if (rows.size() > 0) {
          Map<String, Object> o = rows.get(0);
          ret.put(users[i], o.get("pid"));
        }
      }
    }
    return ret;
  }


  public Object getUserAdminFlag() throws Exception {
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_admin_flag.sql");

      List<Map<String, Object>> rows = scr.query(
              sql, cd.sess.login_user.pid, orgdb.getDatabase());

      if (rows.size() > 0) {
        Map<String, Object> o = rows.get(0);
        return o.get("admin_flag");
      }
    }
    return 0;
  }


  public String getUserIdByOpenId() {
    return cd.sess.login_user.userid;
  }


  public Object getUserOrgList() throws Exception {
    List<Map<String, Object>> ret;
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      ret = scr.query(SqlReader.read("user0003"), cd.sess.login_user.pid);
    }
    return ret;
  }


  public Object getUserLoginExpiration() {
    return SysConfig.me().readConfig().sessionTimeout * 60;
  }


  public String uuid() {
    return Tool.uuid.ds();
  }


  public synchronized long nextId() {
    return id++;
  }


  public String randomNumber() {
    return randomNumber(6);
  }


  public String randomNumber(Integer i) {
    if (i == null) i = 6;
    return (long)(Math.random() * Math.pow(10, i)) + "";
  }


  public String randomDouble(int p, int s) {
    BigDecimal a = BigDecimal.valueOf(Math.random() * Math.pow(10, p));
    return a.setScale(2, BigDecimal.ROUND_DOWN).toString();
  }


  public int randomIntWithMaxValue(int max) {
    return (int)(Math.random() * max);
  }


  public String randomString(int len) {
    return Tool.randomString(len);
  }


  public String pinyinFirstLetter(String zh) {
    return ChineseInital.getAllFirstLetter(zh);
  }


  public String formattedNumber(double v, String f) {
    java.text.DecimalFormat df = new java.text.DecimalFormat(f);
    return df.format(v);
  }


  public Object instanceFromJson(String str) {
    return NativeJSON.parse(this, str, null);
  }


  public Object jsonFromInstance(Object obj) {
    return NativeJSON.stringify(this,
            ScriptUtils.unwrap(obj), null, null);
  }
}
