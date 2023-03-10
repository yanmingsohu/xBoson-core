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
// 文件创建日期: 2017年11月1日 上午11:57:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/SessionCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.been.*;
import com.xboson.db.ConnectConfig;
import com.xboson.event.timer.EarlyMorning;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.service.OAuth2;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.AES;
import com.xboson.util.SessionID;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IOAuth2;


public class SessionCluster extends HttpFilter {

  private static final long serialVersionUID = -6654306025872001022L;
  private static final String COOKIE_NAME = "xBoson";

  private static byte[] sessionPassword = null;
  private static int sessionTimeout = 0; // 分钟

  private final Log log = LogFactory.create("session");
  private String contextPath;
  private ConnectConfig db;


  protected void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain chain)
          throws IOException, ServletException {
    SessionData sd = null;

    try {
      sd = fromToken(request, response);
      if (sd == null) {
        sd = fromCookie(request, response);
      }

      request.setAttribute(SessionData.ATTRNAME, sd);
      chain.doFilter(request, response);

    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    } finally {
      if (sd != null) {
        sd.prolong();
        RedisMesmerizer.me().sleep(sd);
      }
    }
  }


  /**
   * 如果没有 token 参数则返回 null, 如果 token 无效会抛出异常.
   */
  private SessionData fromToken(HttpServletRequest request,
                                HttpServletResponse response)
          throws IOException, ServletException, SQLException
  {
    String token = request.getParameter(IOAuth2.PARM_TOKEN);
    if (Tool.isNulStr(token) || token.length() != IOAuth2.TOKEN_LENGTH) {
      return null;
    }

    SessionData sess = (SessionData)
            RedisMesmerizer.me().wake(SessionData.class, token);
    if (sess != null) {
      return sess;
    }

    AppToken at = OAuth2.openToken(token, db);
    if (at == null) {
      throw new XBosonException("invalid Token: "+ token, 21323);
    }

    LoginUser user = LoginUser.fromDb(at.userid, db);
    if (user == null) {
      throw new XBosonException("Token 对应的用户无法访问: "+ at.userid);
    }
    user.bindUserRoles(db);
    user.password = null;

    sess = new SessionData(at, user);
    log.debug("Token ID:", token);
    return sess;
  }


  /**
   * 总是会尽可能返回一个 SessionData, 在必要时会创建空的 SessionData
   */
  private SessionData fromCookie(HttpServletRequest request,
                                 HttpServletResponse response)
          throws IOException, ServletException
  {
    Cookie ck = SessionID.getCookie(COOKIE_NAME, request);
    SessionData sd = null;

    if (ck == null) {
      //
      // 第一次访问, 创建 cookie
      //
      ck = createCookie(request, response);
    } else {
      //
      // 验证 cookie 的值是否是平台生成
      //
      if (!SessionID.checkSessionId(sessionPassword, ck.getValue()) ) {
        //
        // 错误的 cookie 加密, 则生成全新的 cookie
        //
        log.debug("Bad Session id:", ck.getValue());
        ck = createCookie(request, response);
      } else {
        //
        // 尝试从 redis 还原数据
        //
        sd = resurrectionSession(ck);
        //
        // 超时则重建数据
        //
        if (sd != null && sd.isTimeout()) {
          ck = createCookie(request, response);
          sd = null;
        }
      }
    }

    if (sd == null) {
      sd = new SessionData(ck, sessionTimeout);
    }
    log.debug("ID:", ck.getValue());
    return sd;
  }


  /**
   * 从请求中还原 session
   */
  public static SessionData resurrectionSession(HttpServletRequest request) {
    Cookie ck = SessionID.getCookie(COOKIE_NAME, request);
    return resurrectionSession(ck);
  }


  /**
   * 从 cookie 中还原 session 数据
   */
  public static SessionData resurrectionSession(Cookie ck) {
    return (SessionData) RedisMesmerizer.me()
            .wake(SessionData.class, ck.getValue());
  }


  private Cookie createCookie(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException {
    Cookie ck = new Cookie(COOKIE_NAME,
            SessionID.generateSessionId(sessionPassword));
    ck.setHttpOnly(true);
    ck.setMaxAge(sessionTimeout * 60);
    ck.setPath(contextPath);
    // 如果未设置cookie的domain属性，则该cookie仅适用于其原始域。
    // ck.setDomain(req.getServerName());
    resp.addCookie(ck);
    return ck;
  }


  public void init(FilterConfig filterConfig) throws ServletException {
    Config cfg = SysConfig.me().readConfig();
    sessionTimeout  = cfg.sessionTimeout;
    sessionPassword = AES.aesKey( cfg.sessionPassword );
    contextPath     = filterConfig.getServletContext().getContextPath();
    db              = cfg.db;

    if (cfg.enableSessionClear) {
      SessionData sd = new SessionData();
      TimerTask clean = RedisMesmerizer.me().createCleanTask(sd);
      EarlyMorning.add(clean);
    }
  }
}
