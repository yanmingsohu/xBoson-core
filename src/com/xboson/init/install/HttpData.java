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
// 文件创建日期: 17-11-19 上午8:46
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/HttpData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install;

import com.xboson.been.Config;
import com.xboson.util.Tool;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;


public class HttpData {

  public final HttpServletRequest req;
  public final HttpServletResponse resp;
  public final ServletContext sc;
  public final Config cf;
  public boolean reset = false;
  public boolean ajax = false;

  /**
   * 设定返回给页面的消息
   */
  public String msg;


  public HttpData(HttpServletRequest req, HttpServletResponse resp, Config c) {
    this.req = req;
    this.resp = resp;
    this.sc = req.getServletContext();
    this.cf = c;
  }


  /**
   * 如果参数是空字符串或 null, 则返回 null.
   */
  public String getStr(String name) {
    String n = req.getParameter(name);
    if (Tool.isNulStr(n)) return null;
    return n;
  }


  /**
   * 获取 http 参数, 如果参数为空字符串返回 0
   */
  public int getInt(String name) {
    String n = req.getParameter(name);
    if (Tool.isNulStr(n)) return 0;
    return Integer.parseInt(n);
  }


  public boolean getBool(String name) {
    String b = req.getParameter(name);
    if (Tool.isNulStr(b)) return false;
    if ("1".equals(b)) return true;
    return Boolean.parseBoolean(b);
  }


  /**
   * 是目录返回 true, 否则返回 false 并在 msg 上绑定错误消息.
   */
  public boolean isDirectory(String path) {
    File f = new File(path);
    if (! f.exists()) {
      msg = "目录不存在: " + path;
      return false;
    }
    if (! f.isDirectory()) {
      msg = "不是目录: " + path;
      return false;
    }
    return true;
  }


  public String config() {
    return Tool.getAdapter(Object.class).toJson(cf);
  }
}
