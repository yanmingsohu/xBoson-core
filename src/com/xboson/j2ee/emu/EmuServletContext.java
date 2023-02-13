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
// 文件创建日期: 18-4-18 上午10:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/emu/EmuServletContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.emu;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;


public class EmuServletContext implements ServletContext {

  public String basePath = ".";


  @Override
  public String getContextPath() {
    return "xboson";
  }


  @Override
  public ServletContext getContext(String s) {
    return this;
  }


  @Override
  public int getMajorVersion() {
    return 0;
  }


  @Override
  public int getMinorVersion() {
    return 0;
  }


  @Override
  public int getEffectiveMajorVersion() {
    return 0;
  }


  @Override
  public int getEffectiveMinorVersion() {
    return 0;
  }


  @Override
  public String getMimeType(String s) {
    return null;
  }


  @Override
  public Set<String> getResourcePaths(String s) {
    return null;
  }


  @Override
  public URL getResource(String s) throws MalformedURLException {
    Path p = Paths.get(basePath +"/WebRoot/"+ s);
    return p.toUri().toURL();
  }


  @Override
  public InputStream getResourceAsStream(String s) {
    try {
      return getResource(s).openStream();
    } catch (IOException e) {
      return null;
    }
  }


  @Override
  public RequestDispatcher getRequestDispatcher(String s) {
    return null;
  }


  @Override
  public RequestDispatcher getNamedDispatcher(String s) {
    return null;
  }


  @Override
  public Servlet getServlet(String s) throws ServletException {
    return null;
  }


  @Override
  public Enumeration<Servlet> getServlets() {
    return null;
  }


  @Override
  public Enumeration<String> getServletNames() {
    return null;
  }


  @Override
  public void log(String s) {

  }


  @Override
  public void log(Exception e, String s) {

  }


  @Override
  public void log(String s, Throwable throwable) {

  }


  @Override
  public String getRealPath(String s) {
    return null;
  }


  @Override
  public String getServerInfo() {
    return null;
  }


  @Override
  public String getInitParameter(String s) {
    return null;
  }


  @Override
  public Enumeration<String> getInitParameterNames() {
    return null;
  }


  @Override
  public boolean setInitParameter(String s, String s1) {
    return false;
  }


  @Override
  public Object getAttribute(String s) {
    return null;
  }


  @Override
  public Enumeration<String> getAttributeNames() {
    return null;
  }


  @Override
  public void setAttribute(String s, Object o) {

  }


  @Override
  public void removeAttribute(String s) {

  }


  @Override
  public String getServletContextName() {
    return null;
  }


  @Override
  public ServletRegistration.Dynamic addServlet(String s, String s1) {
    return null;
  }


  @Override
  public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
    return null;
  }


  @Override
  public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
    return null;
  }


  @Override
  public ServletRegistration.Dynamic addJspFile(String s, String s1) {
    return null;
  }


  @Override
  public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
    return null;
  }


  @Override
  public ServletRegistration getServletRegistration(String s) {
    return null;
  }


  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return null;
  }


  @Override
  public FilterRegistration.Dynamic addFilter(String s, String s1) {
    return null;
  }


  @Override
  public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
    return null;
  }


  @Override
  public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
    return null;
  }


  @Override
  public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
    return null;
  }


  @Override
  public FilterRegistration getFilterRegistration(String s) {
    return null;
  }


  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return null;
  }


  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return null;
  }


  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> set) {

  }


  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return null;
  }


  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return null;
  }


  @Override
  public void addListener(String s) {

  }


  @Override
  public <T extends EventListener> void addListener(T t) {

  }


  @Override
  public void addListener(Class<? extends EventListener> aClass) {

  }


  @Override
  public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
    return null;
  }


  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;
  }


  @Override
  public ClassLoader getClassLoader() {
    return null;
  }


  @Override
  public void declareRoles(String... strings) {

  }


  @Override
  public String getVirtualServerName() {
    return null;
  }


  @Override
  public int getSessionTimeout() {
    return 0;
  }


  @Override
  public void setSessionTimeout(int i) {

  }


  @Override
  public String getRequestCharacterEncoding() {
    return null;
  }


  @Override
  public void setRequestCharacterEncoding(String s) {

  }


  @Override
  public String getResponseCharacterEncoding() {
    return null;
  }


  @Override
  public void setResponseCharacterEncoding(String s) {

  }
}
