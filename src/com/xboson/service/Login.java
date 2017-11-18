////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午3:33:52
// 原始文件路径: xBoson/src/com/xboson/service/Login.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.been.CallData;
import com.xboson.been.Config;
import com.xboson.been.LoginUser;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;

import java.sql.ResultSet;
import java.sql.SQLException;


@XPath("/login")
public class Login extends XService {


  /**
   * TODO: 需要检查登录攻击, 失败次数, 验证图片
   * password 参数必须已经 md5 后传入接口.
   */
	@Override
	public void service(CallData data) throws Exception {
	  if (data.sess.login_user != null && data.sess.login_user.userid != null) {
      data.xres.response("Already logged in", 1002);
      return;
    }

    final String md5ps  = data.getString("password", 6, 40);
    final String userid = data.getString("userid",   4, 50);

    Config cf = SysConfig.me().readConfig();
    LoginUser lu = checkRoot(cf, userid, md5ps);

    if (lu == null) {
      lu = searchUser(userid, md5ps, cf.db);
      if (lu == null) {
        data.xres.response("用户不存在", 1014);
        return;
      }
    }

    data.sess.login_user = lu;
    lu.password = null;
    data.xres.response("login success");
	}


	private LoginUser searchUser(String userid, String md5ps, ConnectConfig db)
          throws SQLException {
    //
    // 把 userid 分别假设为 userid/tel/email 查出哪个算哪个
    //
    String[] parmbind = new String[] {userid, userid, userid};
    LoginUser lu = null;

    try (SqlResult sr = SqlReader.query("login.sql", db, parmbind)) {
      ResultSet rs = sr.getResult();
      while (rs.next()) {
        int c = rs.getInt("c");
        if (c == 1) {
          lu              = new LoginUser();
          lu.pid          = rs.getString("pid");
          lu.userid       = rs.getString("userid");
          lu.password     = rs.getString("password");
          lu.password_dt  = rs.getString("password_dt");
          lu.tel          = rs.getString("tel");
          lu.email        = rs.getString("email");
          break;
        }
      }
    }

    if (lu != null) {
      final String ps = Password.v1(lu.userid, md5ps, lu.password_dt);
      if (!ps.equals(lu.password)) {
        throw new XBosonException("用户名或密码错误", 1001);
      }
    }

    return lu;
  }


  private Root checkRoot(Config cf, String nm, String md5ps) {
	  if (!cf.rootUserName.equals(nm))
	    return null;

    final String cps = Password.v1(nm, md5ps);
    if (!cps.equals(cf.rootPassword)) {
      return null;
    }

    Root root = new Root();
    root.userid = nm;
    return root;
  }


	@Override
	public boolean needLogin() {
		return false;
	}


	static private final class Root extends LoginUser {
	  public boolean isRoot() {
	    return true;
    }
  }
}
