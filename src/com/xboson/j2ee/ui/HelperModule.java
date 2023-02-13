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
// 文件创建日期: 18-1-21 上午10:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/HelperModule.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.auth.impl.ResourceRoleTypes;
import com.xboson.auth.impl.RoleBaseAccessControl;
import com.xboson.been.LoginUser;
import com.xboson.been.SessionData;
import com.xboson.j2ee.container.SessionCluster;
import com.xboson.script.EventLoop;
import com.xboson.script.IVisitByScript;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import okhttp3.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 专门为 masquerade 提供辅助函数
 */
public class HelperModule implements IVisitByScript {

  private ThreadLocal<SessionData> sessions = new ThreadLocal<>();
  private OkHttpClient hc;
  private static AtomicLong id = new AtomicLong(0);


  public long nextId() {
    return id.incrementAndGet();
  }


  /**
   * 在线程变量上恢复 sessions 数据
   */
  public void login(HttpServletRequest request) {
    sessions.set(SessionCluster.resurrectionSession(request));
  }


  /**
   * 返回页面权限信息, 调用该方法前必须正确的调用 login()
   * @see #login(HttpServletRequest)
   */
  public Object pageAccessInfo(String pageid) {
    SessionData data = sessions.get();
    Object ret = null;
    if (data != null) {
      LoginUser user = data.login_user;
      if (user != null) {
        ret = RoleBaseAccessControl.check(
                user, ResourceRoleTypes.PAGE, pageid, false);
      }
    }
    return ret;
  }


  public Object httpGet(String url, ScriptObjectMirror header)
          throws IOException {
    Request.Builder req = new Request.Builder();
    req.url(HttpUrl.parse(url));
    for (Map.Entry<String, Object> ks : header.entrySet()) {
      req.addHeader(ks.getKey(), String.valueOf(ks.getValue()));
    }
    Response resp = openClient().newCall(req.build()).execute();
    return resp.body().string();
  }


  public Object httpPost(String url, ScriptObjectMirror header, String body)
          throws IOException {
    Request.Builder req = new Request.Builder();
    req.url(HttpUrl.parse(url));
    req.post(RequestBody.create(
            MediaType.parse("text/plain"),
            body == null ? IConstant.NULL_STR : body));

    for (Map.Entry<String, Object> ks : header.entrySet()) {
      req.addHeader(ks.getKey(), String.valueOf(ks.getValue()));
    }
    Response resp = openClient().newCall(req.build()).execute();
    return resp.body().string();
  }


  private OkHttpClient openClient() {
    if (hc == null) {
      hc = new OkHttpClient();
    }
    return hc;
  }


  public EventLoop createEventLoop(ScriptObjectMirror process) {
    return new EventLoop(process);
  }


  /**
   * 调用函数, 在函数返回前在 locker 对象上应用锁, 直到函数返回.
   */
  public Object lockCall(Object locker, ScriptObjectMirror func, Object... args) {
    if (locker == null) locker = this;
    synchronized (locker) {
      return func.call(null, args);
    }
  }


  public Map<?,?> newSyncMap() {
    return new ConcurrentHashMap<>();
  }
}
