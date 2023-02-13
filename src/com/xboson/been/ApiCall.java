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
// 文件创建日期: 17-12-4 上午8:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ApiCall.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * 封装对 api 的调用, 参数小写化.
 */
public class ApiCall implements IBean {

  /** 必须, HTTP 请求参数 */
  public CallData call;

  /** 必须, 机构 id, 该参数可能在运行时被替换, exparam 始终保存请求值 */
  public String org;

  /** 必须, 应用 id */
  public String app;

  /** 必须, 模块 id */
  public String mod;

  /** 必须, 接口 id */
  public String api;

  /** 扩展请求参数, 优先级高于 http 参数, 可以 null */
  public Map<String, Object> exparam;


  /**
   * 分析 url 参数, 并将请求映射到 api 上, 返回的对象中 call 属性为 null.
   * @param url 该参数是安全的, 不会被改变.
   */
  public ApiCall(UrlSplit url) {
    UrlSplit sp = url.clone();
    sp.withoutSlash(true);

    this.org = sp.next().toLowerCase();
    this.app = sp.next().toLowerCase();
    this.mod = sp.next().toLowerCase();
    this.api = sp.next().toLowerCase();
  }


  /**
   * 参数都被转换为小写.
   */
  public ApiCall(String org, String app, String mod, String api) {
    this.org = toLower(org);
    this.app = toLower(app);
    this.mod = toLower(mod);
    this.api = toLower(api);
  }


  private String toLower(String s) {
    return s == null ? null : s.toLowerCase();
  }


  /**
   * <b>谨慎调用 !!</b><br/>
   * 线程被 kill, 不能正常应答(抛异常或发送错误消息都不可用), 这里发送最后一条消息,
   * 防止浏览器不停的请求这个没有应答的 api.
   */
  public void makeLastMessage(String msg) {
    try {
      PrintWriter out = call.resp.getWriter();
      out.write('"');
      out.write(msg);
      out.write('"');
      out.flush();
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }
}
