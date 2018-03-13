////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-3-13 上午11:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/service/OAuth2.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.been.*;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.IConstant;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;


@XPath("/oauth2")
public class OAuth2 extends XService {

  public static final String MSG
          = "Provide sub-service name '/oauth2/[sub service]'";

  public static final String PageBase
          = "/xboson/face/ui/paas/oauth2/";

  public static final String MODE = "authorization_code";

  public static final int CODE_LENGTH  = 40;
  public static final int TOKEN_LENGTH = 90;
  /** 授权码有效期, 10 分钟, 单位毫秒 */
  public static final int CODE_LIFE    = 10 * 60 * 1000;
  /** 令牌有效期, 90 天, 单位秒 */
  public static final int TOKEN_LIFE   = 90 * 24 * 60;


  private Config cf;


  public OAuth2() {
    cf = SysConfig.me().readConfig();
  }


  @Override
  public void service(CallData data) throws Exception {
    subService(data, MSG);
  }


  @Override
  public boolean needLogin() {
    return false;
  }


  private boolean isLogin(CallData data) {
    return data.sess.login_user != null
            && data.sess.login_user.userid != null;
  }


  /**
   * 获取授权码
   */
  public void authorize(CallData data) throws Exception {
    try {
      if (! isLogin(data)) {
        goPage(data, "login.html");
        return;
      }
      String type = data.getString("grant_type", 1, 30);
      String cid  = data.getString("client_id", 1, 99);
      String stat = data.req.getParameter("state");

      if (! MODE.equals(type)) {
        goPage(data, "badtype.html");
        return;
      }

      String uri = null;
      String appnm = null;
      boolean enable = false;

      try (SqlResult sr = SqlReader.query(
              "open_tp_app.sql", cf.db, cid)) {
        ResultSet rs = sr.getResult();
        if (rs.next()) {
          uri    = rs.getString("uri");
          appnm  = rs.getString("tp_appnm");
          enable = rs.getInt("status") > 0;
        }
      }

      if (enable == false && uri != null) {
        goPage(data, "block.html");
        return;
      }

      String code = Tool.randomString2(CODE_LENGTH);
      OAuth2Code ocode = new OAuth2Code(code);
      ocode.clientid   = cid;
      ocode.begin      = System.currentTimeMillis();
      ocode.userid     = data.sess.login_user.userid;
      RedisMesmerizer.me().sleep(ocode);

      goPage(data, "access.html",
              "code", code, "uri", uri, "state", stat,
              "appnm", appnm, "usernm", data.sess.login_user.userid);

    } catch(XBosonException.BadParameter e) {
      goPage(data, "badp.html");
    } catch(Exception e) {
      data.resp.getWriter().write("Error: " + e.getMessage());
    }
  }


  /**
   * 用授权码交换令牌
   */
  public void access_token(CallData data) throws Exception {
    try {
      String type = data.getString("grant_type", 1, 30);
      String cid  = data.getString("client_id", 1, 99);
      String ps   = data.getString("client_secret", 1, 99);
      String code = data.getString("code", 1, CODE_LENGTH);

      if (! MODE.equals(type)) {
        error(data, 21328, "unsupported_grant_type");
        return;
      }

      OAuth2Code ocode =
              (OAuth2Code) RedisMesmerizer.me().wake(OAuth2Code.class, code);
      if (ocode == null) {
        error(data, 21325, "invalid_grant");
        return;
      } else {
        RedisMesmerizer.me().remove(ocode);
      }

      if (! cid.equals(ocode.clientid)) {
        error(data, 21324, "invalid_client");
        return;
      }

      if (! checkClient(cid, ps)) {
        error(data, 21324, "invalid_client");
        return;
      }

      Date birth = new Date(System.currentTimeMillis());
      AppToken at  = new AppToken(birth, TOKEN_LIFE);
      at.clientid  = ocode.clientid;
      at.token     = Tool.randomString2(TOKEN_LENGTH);
      at.userid    = ocode.userid;

      if (! saveTokenToDB(at, birth)) {
        error(data, 21324, "invalid_client");
      }
      RedisMesmerizer.me().sleep(at);

      data.xres.bindResponse("access_token", at.token);
      data.xres.bindResponse("expires_in", TOKEN_LIFE);
      data.xres.bindResponse("userid", ocode.userid);
      data.xres.responseMsg("ok", 0);

    } catch(XBosonException.BadParameter e) {
      error(data, 21323, "invalid_request", e.getMessage());
    }
  }


  /**
   * 撤销令牌
   */
  public void revoke_token(CallData data) throws Exception {
    try {
      String cid   = data.getString("client_id", 1, 99);
      String ps    = data.getString("client_secret", 1, 99);
      String token = data.getString("access_token", 1, TOKEN_LENGTH);

      if (! checkClient(cid, ps)) {
        error(data, 21324, "invalid_client");
        return;
      }

      try (SqlResult sr = SqlReader.query(
              "delete_app_token.sql", cf.db, token, cid)) {
        if (sr.getUpdateCount() != 1) {
          error(data, 21327, "expired_token");
          return;
        }
      }

      AppToken at = new AppToken();
      at.token = token;
      RedisMesmerizer.me().remove(at);

      data.xres.responseMsg("ok", 0);
    } catch(XBosonException.BadParameter e) {
      error(data, 21323, "invalid_request", e.getMessage());
    }
  }


  private boolean checkClient(String clientId, String clientPs)
          throws SQLException {
    boolean enable = false;

    try (SqlResult sr = SqlReader.query(
            "open_tp_app_ps.sql", cf.db, clientId, clientPs)) {
      ResultSet rs = sr.getResult();
      if (rs.next()) {
        enable = rs.getInt("status") > 0;
      }
    }
    return enable;
  }


  private boolean saveTokenToDB(AppToken at, Date birth) {
    try (SqlResult sr = SqlReader.query(
            "create_app_token.sql", cf.db,
            at.clientid, at.token, at.userid, birth, TOKEN_LIFE, 1)) {
      return (sr.getUpdateCount() == 1);
    }
  }


  private void goPage(CallData data, String page, Object...p) throws Exception {
    StringBuilder uri = new StringBuilder();
    uri.append(PageBase);
    uri.append(page);

    if (p != null && p.length > 0) {
      uri.append("?");
      for (int i=0; i<p.length; i+=2) {
        uri.append(p[i]);
        uri.append('=');
        if (p[i+1] != null) {
          uri.append(java.net.URLEncoder.encode(
                  String.valueOf(p[i+1]), IConstant.CHARSET_NAME));
        }
        uri.append('&');
      }
    }

    data.resp.sendRedirect(uri.toString());
  }


  private void error(CallData data, int code, String err) throws IOException {
    error(data, code, err, err);
  }


  private void error(CallData data, int code, String err, String msg)
          throws IOException {
    data.xres.bindResponse("error", err);
    data.xres.bindResponse("error_code", code);
    data.xres.responseMsg(msg, code);
  }
}
