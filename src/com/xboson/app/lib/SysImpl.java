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
import com.xboson.j2ee.resp.XmlResponse;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Uuid;
import com.xboson.util.*;
import com.xboson.util.converter.ScriptObjectMirrorJsonConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;


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
  private List<Object> printList;


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
      retData.put(key, new ScriptObjectMirrorJsonConverter.Warp(o));
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


  public String getUserPID(String userid) throws Exception {
    try (SqlCachedResult scr = new SqlCachedResult(orgdb)) {
      String sql = SqlReader.read("user_id_to_pid.sql");
      List<Map<String, Object>> rows = scr.query(sql, userid);
      if (rows.size() > 0) {
        Map<String, Object> o = rows.get(0);
        return (String) o.get("pid");
      }
    }
    return null;
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
    return Tool.nextId();
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
    return a.setScale(s, BigDecimal.ROUND_DOWN).toString();
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


  public Object instanceFromXml(String xmlstr) {
    return Tool.createXmlStream().fromXML(xmlstr);
  }


  public Object xmlFromInstance(Object obj) throws IOException {
    ScriptObjectMirrorJsonConverter.Warp warp
            = new ScriptObjectMirrorJsonConverter.Warp(obj);

    Writer out = new StringWriter();
    out.append(XmlResponse.XML_HEAD);
    Tool.createXmlStream().toXML(warp, out);
    return out.toString();
  }


  public Object emptyToNull(String str) {
    if (str == null) return null;
    str = str.trim();
    if (str.length() <= 0) return null;
    if (str.equalsIgnoreCase("NULL")) return null;
    return str;
  }


  public Object isNumber(Object v) {
    try {
      Double.parseDouble(v.toString());
      return true;
    } catch(Exception e) {
      return false;
    }
  }


  public Object parseInt(Object v) {
    try {
      return Integer.parseInt(v.toString());
    } catch(Exception e) {
      return 0;
    }
  }


  public Object executeJavaScript(Object a, Object b) {
    throw new UnsupportedOperationException("executeJavaScript");
  }


  public void printValue(Object v) {
    if (printList == null) {
      printList = new ArrayList<>();
      cd.xres.bindResponse("print", printList);
    }
    printList.add(v);
  }


  /**
   * 包装 java byte 数组, 返回 js 数组类型对象.
   *
   * [实现细节] 使用 ArrayData.allocate(ByteBuffer.wrap(b)) 创建的数组对象,
   * 虽然不需要复制内存, 但是无法再次调用 java 函数, 如果调用抛出
   * UnsupportedOperationException 异常.
   */
  private Object _warp_(byte[] b) {
    NativeArray na = NativeArray.construct(true, null, b.length);
    ScriptObjectMirror js = (ScriptObjectMirror)
            ScriptObjectMirror.wrap(na, Context.getGlobal());

    for (int i=0; i<b.length; ++i) {
      js.setSlot(i, b[i]);
    }
    return js;
  }


  public Object bytes(String s) {
    return _warp_( s.getBytes(IConstant.CHARSET) );
  }


  public Object encodeBase64(String v) {
    return encodeBase64( v.getBytes(IConstant.CHARSET) );
  }


  public Object encodeBase64(byte[] b) {
    return _warp_( Base64.getEncoder().encode(b) );
  }


  public Object encodeBase64(Buffer.JsBuffer buf) {
    return encodeBase64( buf._buffer().array() );
  }


  public String encodeBase64String(String v) {
    return encodeBase64String( v.getBytes(IConstant.CHARSET) );
  }


  public String encodeBase64String(byte[] b) {
    return Base64.getEncoder().encodeToString(b);
  }


  public String encodeBase64String(Buffer.JsBuffer buf) {
    return encodeBase64String( buf._buffer().array() );
  }


  public Object decodeBase64(String v) {
    return _warp_( Base64.getDecoder().decode(v) );
  }


  public Object decodeBase64(byte[] b) {
    return _warp_( Base64.getDecoder().decode(b) );
  }


  public Object decodeBase64(Buffer.JsBuffer buf) {
    return decodeBase64( buf._buffer().array() );
  }


  public String decodeBase64String(String v) {
    return new String(Base64.getDecoder().decode(v), IConstant.CHARSET);
  }


  public String decodeBase64String(byte[] v) {
    return new String(Base64.getDecoder().decode(v), IConstant.CHARSET);
  }


  public String decodeBase64String(Buffer.JsBuffer buf) {
    return decodeBase64String( buf._buffer().array() );
  }


  public String md5(String v) {
    return Hex.upperHex(Password.md5(v));
  }


  public String encrypt(String content, String ps) throws Exception {
    AES2 ekey = new AES2(ps);
    return ekey.encrypt(content);
  }


  public String decrypt(String cipher_text, String ps) {
    AES2 ekey = new AES2(ps);
    return new String(ekey.decrypt(cipher_text), IConstant.CHARSET);
  }


  public boolean regexFind(String regex, String str) {
    return Pattern.matches(regex, str);
  }


  public Object regexSplit(String regex, String str) {
    return str.split(regex);
  }


  public String regexReplaceFirst(String regex, String str, String repl) {
    return str.replaceFirst(regex, repl);
  }


  public String regexReplaceAll(String regex, String str, String repl) {
    return str.replaceAll(regex, repl);
  }


  public Object lotteryRate(int[] list, int[] ign) {

  }
}
