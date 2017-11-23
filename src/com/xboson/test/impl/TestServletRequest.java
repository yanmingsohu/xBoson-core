////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/impl/TestServletRequest.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test.impl;

import com.xboson.util.IConstant;
import com.xboson.util.Tool;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;


public class TestServletRequest implements HttpServletRequest {

  Map<String, Object> attr = new HashMap<>();


  @Override
  public String getAuthType() {
    return null;
  }


  @Override
  public Cookie[] getCookies() {
    return new Cookie[0];
  }


  @Override
  public long getDateHeader(String s) {
    return 0;
  }


  @Override
  public String getHeader(String s) {
    return null;
  }


  @Override
  public Enumeration<String> getHeaders(String s) {
    return null;
  }


  @Override
  public Enumeration<String> getHeaderNames() {
    return null;
  }


  @Override
  public int getIntHeader(String s) {
    return 0;
  }


  @Override
  public String getMethod() {
    return null;
  }


  @Override
  public String getPathInfo() {
    return null;
  }


  @Override
  public String getPathTranslated() {
    return null;
  }


  @Override
  public String getContextPath() {
    return "/xboson";
  }


  @Override
  public String getQueryString() {
    return null;
  }


  @Override
  public String getRemoteUser() {
    return null;
  }


  @Override
  public boolean isUserInRole(String s) {
    return false;
  }


  @Override
  public Principal getUserPrincipal() {
    return null;
  }


  @Override
  public String getRequestedSessionId() {
    return null;
  }


  @Override
  public String getRequestURI() {
    return getContextPath() + "/app/";
  }


  @Override
  public StringBuffer getRequestURL() {
    return null;
  }


  @Override
  public String getServletPath() {
    return null;
  }


  @Override
  public HttpSession getSession(boolean b) {
    return null;
  }


  @Override
  public HttpSession getSession() {
    return null;
  }


  @Override
  public String changeSessionId() {
    return null;
  }


  @Override
  public boolean isRequestedSessionIdValid() {
    return false;
  }


  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return false;
  }


  @Override
  public boolean isRequestedSessionIdFromURL() {
    return false;
  }


  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }


  @Override
  public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
    return false;
  }


  @Override
  public void login(String s, String s1) throws ServletException {

  }


  @Override
  public void logout() throws ServletException {

  }


  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return null;
  }


  @Override
  public Part getPart(String s) throws IOException, ServletException {
    return null;
  }


  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
    return null;
  }


  @Override
  public Object getAttribute(String s) {
    return attr.get(s);
  }


  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(attr.keySet());
  }


  @Override
  public String getCharacterEncoding() {
    return IConstant.CHARSET_NAME;
  }


  @Override
  public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
    throw new UnsupportedEncodingException();
  }


  @Override
  public int getContentLength() {
    return 0;
  }


  @Override
  public long getContentLengthLong() {
    return 0;
  }


  @Override
  public String getContentType() {
    return "html/text";
  }


  @Override
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }


  @Override
  public String getParameter(String s) {
    if ("$format".equals(s)) {
      return "json";
    }
    return Tool.randomString(10);
  }


  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.emptyEnumeration();
  }


  @Override
  public String[] getParameterValues(String s) {
    return new String[0];
  }


  @Override
  public Map<String, String[]> getParameterMap() {
    return null;
  }


  @Override
  public String getProtocol() {
    return null;
  }


  @Override
  public String getScheme() {
    return null;
  }


  @Override
  public String getServerName() {
    return null;
  }


  @Override
  public int getServerPort() {
    return 0;
  }


  @Override
  public BufferedReader getReader() throws IOException {
    return null;
  }


  @Override
  public String getRemoteAddr() {
    return null;
  }


  @Override
  public String getRemoteHost() {
    return null;
  }


  @Override
  public void setAttribute(String s, Object o) {
    attr.put(s, o);
  }


  @Override
  public void removeAttribute(String s) {
    attr.remove(s);
  }


  @Override
  public Locale getLocale() {
    return null;
  }


  @Override
  public Enumeration<Locale> getLocales() {
    return null;
  }


  @Override
  public boolean isSecure() {
    return false;
  }


  @Override
  public RequestDispatcher getRequestDispatcher(String s) {
    return null;
  }


  @Override
  public String getRealPath(String s) {
    return null;
  }


  @Override
  public int getRemotePort() {
    return 0;
  }


  @Override
  public String getLocalName() {
    return null;
  }


  @Override
  public String getLocalAddr() {
    return null;
  }


  @Override
  public int getLocalPort() {
    return 0;
  }


  @Override
  public ServletContext getServletContext() {
    return null;
  }


  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    return null;
  }


  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    return null;
  }


  @Override
  public boolean isAsyncStarted() {
    return false;
  }


  @Override
  public boolean isAsyncSupported() {
    return false;
  }


  @Override
  public AsyncContext getAsyncContext() {
    return null;
  }


  @Override
  public DispatcherType getDispatcherType() {
    return null;
  }
}
