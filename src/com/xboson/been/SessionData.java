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
// 文件创建日期: 2017年11月2日 下午2:43:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/SessionData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.sleep.IBinData;
import com.xboson.sleep.ITimeout;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * Session 不是动态数据, 属性都是固定的,
 * 这与 servlet 中的 session 是分离的两套系统, xBoson 不使用 servlet.session.
 */
public class SessionData implements IBean, IBinData, ITimeout {

  public static final String ATTRNAME = "xBoson-session-data";

  public LoginUser login_user;
  public String id;
  public String captchaCode;

  public long loginTime;
  public long endTime;
  private long duration;


  public SessionData() {
  }


  /**
   * 使用 token 创建 session
   */
  public SessionData(AppToken token, LoginUser user) {
    this.id = token.token;
    this.login_user = user;
    this.loginTime = user.loginTime;
    this.endTime = token.over;
    this.duration = endTime - loginTime;
  }


  /**
   * 使用 cookie 创建 session
   */
  public SessionData(Cookie ck, int sessionTimeoutMinute) {
    this.id = ck.getValue();
    this.duration = sessionTimeoutMinute * 60 * 1000;
    this.loginTime = System.currentTimeMillis();
    this.endTime = this.loginTime + this.duration;
  }


  public boolean isTimeout() {
    return System.currentTimeMillis() > endTime;
  }


  public static SessionData get(HttpServletRequest request) throws ServletException {
    SessionData sd = (SessionData) request.getAttribute(ATTRNAME);
    if (sd == null) {
      throw new ServletException("SessionData not init");
    }
    return sd;
  }


  @Override
  public String getid() {
    return id;
  }


  /**
   * 标记为销毁状态
   */
  public void destoryFlag() {
    endTime = 0;
  }


  /**
   * 延长 session 有效期
   */
  public void prolong() {
    endTime = System.currentTimeMillis() + duration;
  }
}
