////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/HttpImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.util.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * 每个请求一个实例
 */
public class HttpImpl extends RuntimeUnitImpl {

  private Map<String, Cookie> cookies;


  public HttpImpl(CallData cd) {
    super(cd);
  }


  public void setStatusCode(int code) {
    cd.resp.setStatus(code);
  }


  public String schema() {
    return cd.req.getScheme();
  }


  public String domain() {
    String domain = cd.req.getHeader("Host");
    if (domain == null) {
      domain = cd.req.getServerName();
    }
    return domain;
  }


  public int port() {
    return cd.req.getServerPort();
  }


  public String remoteIp() {
    return cd.req.getRemoteAddr();
  }


  public String method() {
    return cd.req.getMethod();
  }


  public Object headers() {
    ScriptObjectMirror js = createJSObject();
    Enumeration<String> it = cd.req.getHeaderNames();
    while (it.hasMoreElements()) {
      String name = it.nextElement();
      String val = cd.req.getHeader(name);
      js.setMember(name, val);
    }
    return js;
  }


  private void parseCookie() {
    cookies = new HashMap<>();
    Cookie[] arr = cd.req.getCookies();

    for (int i=0; i<arr.length; ++i) {
      Cookie ck = arr[i];
      cookies.put(ck.getName(), ck);
    }
  }


  public String getCookie(String name) {
    if (cookies == null) {
      parseCookie();
    }
    return cookies.get(name).getValue();
  }


  public void setCookie(String name, String value) {
    setCookie(name, value, 900, "/");
  }


  public void setCookie(String name, String value, int maxAgeSecond) {
    setCookie(name, value, maxAgeSecond, "/");
  }


  public void setCookie(String name, String value, int maxAgeSecond, String path) {
    Cookie ck = new Cookie(name, value);
    ck.setMaxAge(maxAgeSecond);
    ck.setPath(path);
    cd.resp.addCookie(ck);
  }


  public String encode(String val) throws UnsupportedEncodingException {
    return encode(val, IConstant.CHARSET_NAME);
  }


  public String encode(String val, String charset)
          throws UnsupportedEncodingException {
    return URLEncoder.encode(val, charset);
  }


  public String decode(String val) throws UnsupportedEncodingException {
    return decode(val, IConstant.CHARSET_NAME);
  }


  public String decode(String val, String charset)
          throws UnsupportedEncodingException {
    return URLDecoder.decode(val, charset);
  }


  public Object platformGet(Object api, Object param, Object header) {
    return null;
  }


  public Object platformPost(Object api, Object body, Object param, Object header) {
    return null;
  }


  public Object get(String url, Object param, String type, Object header) {
    return null;
  }


  public Object post(String url, Object body, Object param, String type, Object header) {
    return null;
  }


  public Object asyncPlatformGet(Object...p) {
    throw new UnsupportedOperationException("asyncPlatformGet");
  }


  public Object asyncPlatformPost(Object...p) {
    throw new UnsupportedOperationException("asyncPlatformPost");
  }
}
