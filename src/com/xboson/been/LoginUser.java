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
// 文件创建日期: 17-11-14 上午8:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/LoginUser.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.auth.IAWho;
import com.xboson.db.ConnectConfig;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Hash;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.xboson.util.c0nst.IConstant.MULTI_LOGIN;


public class LoginUser extends JsonHelper implements IBean, IAWho {
  public String pid;        // uuid
  public String userid;     // 就是用户登录 name, 唯一, 也是 openid
  public String tel;
  public String email;
  public String status;
  public long   loginTime;

  transient public String password;
  transient public String password_dt;

  public List<String> roles;
  private boolean multi_login;


  @Override
  public String identification() {
    return pid;
  }


  @Override
  public boolean isRoot() {
    return false;
  }


  /**
   * 检查输入的密码是否与当前用户密码一致
   * @param md5ps 密码明文经过 md5 后的字符串
   * @return 密码一致返回 true
   */
  public boolean checkPS(String md5ps) {
    final String ps = Password.v1(userid, md5ps, password_dt);
    return ps.equals(password);
  }


  public static LoginUser fromDb(String userid, ConnectConfig db)
          throws SQLException {
    return fromDb(userid, db, null);
  }


  /**
   * 从数据库中恢复用户
   *
   * @param userid 用户 id
   * @param db 数据库连接配置
   * @param ps 登录密码, 可以空; 该参数用于检查是否超级管理员.
   * @return 找不到返回 null
   * @throws SQLException
   */
  public static LoginUser fromDb(String userid, ConnectConfig db, String ps)
          throws SQLException
  {
    //
    // 该算法和登录放在一起方便混淆
    //
    Config cf = SysConfig.me().readConfig();
    boolean isRoot = false;
    if (Tool.notNulStr(ps) && userid.equals(cf.rootUserName)) {
      Hash h = new Hash();
      h.update(userid);
      h.update(ps);
      h.update("Fm=κqm1qm2/γ2r <Magnetic coulomb law>");
      isRoot = h.digestStr().equals(cf.rootPassword);
    }

    //
    // 把 userid 分别假设为 userid/tel/email 查出哪个算哪个
    //
    Object[] parmbind = new Object[] {userid, userid, userid};
    LoginUser lu = null;

    try (SqlResult sr = SqlReader.query("login.sql", db, parmbind)) {
      ResultSet rs = sr.getResult();
      while (rs.next()) {
        int c = rs.getInt("c");
        if (c == 1) {
          userid          = rs.getString("userid");
          lu              = isRoot
                          ? new Root() : new LoginUser();
          lu.pid          = rs.getString("pid");
          lu.userid       = userid;
          lu.password     = rs.getString("password");
          lu.password_dt  = rs.getString("password_dt");
          lu.tel          = rs.getString("tel");
          lu.email        = rs.getString("email");
          lu.status       = rs.getString("status");
          lu.loginTime    = System.currentTimeMillis();
          lu.multi_login  = MULTI_LOGIN.equals(rs.getString("multiflag"));
          break;
        }
      }
    }
    return lu;
  }


  /**
   * 绑定当前用户在所有机构中的角色
   * @param db 数据库配置
   */
  public void bindUserRoles(ConnectConfig db) throws SQLException {
    try (SqlResult sr = SqlReader.query("mdm_org", db)) {
      ResultSet orgs = sr.getResult();
      List<String> roles = new ArrayList<>();

      while (orgs.next()) {
        String orgid = orgs.getString("id");

        //
        // schema 不支持变量绑定, 只能拼.
        //
        String sql = "Select roleid From " + orgid
                + ".sys_user_role Where pid=? And status='1'";

        SqlResult sr2 = sr.query(sql, pid);
        ResultSet role_rs = sr2.getResult();

        while (role_rs.next()) {
          roles.add(role_rs.getString("roleid"));
        }
      }

      this.roles = roles;
    }
  }


  /**
   * 把用户绑定到 session 上;
   * 检查用户多点登录, 如果当前用户允许多点登录则什么都不做, 否则将之前登录的客户端登出.
   */
  public void bindTo(SessionData sess) {
    if (pid == null) throw new NullPointerException("not login");
    sess.login_user = this;

    if (! multi_login) {
      try (IRedis cli = RedisMesmerizer.me().open()) {
        String prv_sid = cli.hget(IConstant.REDIS_KEY_MULTI_LOGIN, pid);
        if (! Tool.isNulStr(prv_sid)) {
          //
          // 删除另一个客户端的 session
          //
          cli.hdel(RedisMesmerizer.KEY,
                  RedisMesmerizer.genid(
                          sess.getClass(), prv_sid, RedisMesmerizer.BINARY));
        }
        cli.hset(IConstant.REDIS_KEY_MULTI_LOGIN, pid, sess.getid());
      }
    }
  }


  public String toString() {
    return "[PID: "+ pid +", USERID: "+ userid +"]";
  }


  /**
   * 超级用户
   */
  static private final class Root extends LoginUser {
    public boolean isRoot() {
      return true;
    }
  }
}
