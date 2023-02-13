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
// 文件创建日期: 2017年11月2日 下午3:33:52
// 原始文件路径: xBoson/src/com/xboson/service/UserService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.app.AppContext;
import com.xboson.been.*;
import com.xboson.db.IDict;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;
import com.xboson.log.slow.AccessLog;
import com.xboson.log.slow.LoginLog;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Firewall;
import com.xboson.util.JavaConverter;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.Set;

import static com.xboson.util.Firewall.IP_BAN_COUNT;
import static com.xboson.util.Firewall.IP_NEED_CAPTCHA;
import static com.xboson.util.Firewall.IP_WAIT_TIMEOUT;


@XPath("/user")
public class UserService extends XService implements IDict, IConstant {

  public static final String MSG
          = "Provide sub-service name '/user/[sub service]'";

  /**
   * 跳过登录检查的服务名称列表
   */
  private static final Set<String> skipCheckLogin = JavaConverter.param2set(
          "get_havinguser",
          "register"
  );


  private Config cf;
  private AccessLog access;
  private LoginLog sign;


  public UserService() {
    cf = SysConfig.me().readConfig();
    access = new AccessLog();
    sign = new LoginLog();
  }


  /**
   * 除了登录/登出方法外, 其他 api 都是写在脚本中的.
   */
  public void service(CallData data) throws Exception {
    try {
      subService(data, MSG);
    } catch (XBosonException.NoService ns) {
      OpenApp.banAnonymous(data);

      ApiCall ac = new ApiCall(
        data.getString("org", 0, 100),
        data.getString("app", 1, 100),
        data.getString("mod", 1, 100),
        ns.getServiceName()
      );
      ac.call = data;

      if (skipCheckLogin.contains(ac.api)
              && ac.app.startsWith(SYS_APP_PREFIX)
              && ac.mod.startsWith(SYS_MOD_PREFIX)) {
        AppContext.me().call(ac);
      } else {
        checkLoging(data);
        AppContext.me().call(ac);
      }
    }
  }


  private void msg(CallData data, String msg, int code) throws IOException {
    data.xres.responseMsg(msg, code);
    if (data.sess.login_user != null) {
      access.log(data.sess.login_user, code, msg);
    } else {
      access.log(data.getString("userid", 0, 50), code, msg);
    }
  }


  /**
   * 检查登录攻击, 失败次数, 验证图片
   * password 参数必须已经 md5 后传入接口.
   *
   * @see com.xboson.test.TestSign#admin() 计算 root 用户密钥.
   */
	public void login(CallData data) throws Exception {
	  if (isLogin(data) && !OpenApp.isAnonymousUser(data)) {
      data.xres.bindResponse("openid", data.sess.login_user.userid);
      msg(data, "用户已经登录", 0);
      return;
    }

    Firewall fw = Firewall.me();
    final int login_count = fw.checkIpBan(data.req.getRemoteAddr());
    if (login_count >= IP_BAN_COUNT) {
      msg(data, "登录异常, 等待"+ IP_WAIT_TIMEOUT +"分钟后重试", 997);
	    return;
    }

    final String md5ps  = data.getString("password", 6, 40);
    final String userid = data.getString("userid",   4, 50);

    if (login_count >= IP_NEED_CAPTCHA) {
      String c = data.getString("c", 0, 20);
      if (! c.equalsIgnoreCase( data.sess.captchaCode )) {
        msg(data, "验证码错误", 10);
        fw.ipLoginFail(data.req.getRemoteAddr());
        return;
      }
    }

    OpenApp.banAnonymous(userid);

    LoginUser lu = LoginUser.fromDb(userid, cf.db, md5ps);
    if (lu == null) {
      msg(data, "用户不存在", 1014);
      fw.ipLoginFail(data.req.getRemoteAddr());
      return;
    }
    if (! ZR001_ENABLE.equals(lu.status)) {
      msg(data, "用户已锁定", 1004);
      fw.ipLoginFail(data.req.getRemoteAddr());
      return;
    }
    if (! lu.checkPS(md5ps)) {
      access.log(userid, 1001, "用户名或密码错误");
      throw new XBosonException("用户名或密码错误", 1001);
    }

    lu.password = null;
    lu.bindUserRoles(cf.db);
    lu.bindTo(data.sess);
    data.xres.bindResponse("openid", lu.userid);
    sign.login(lu, data);
    msg(data, "成功登录系统", 0);
	}


	public void logout(CallData data) throws Exception {
    if (!isLogin(data)) {
      data.xres.responseMsg("未登录", 1000);
      return;
    }

    try {
      msg(data, "已登出", 1007);
    } finally {
      data.sess.login_user = null;
      data.sess.destoryFlag();
    }
  }


  private boolean isLogin(CallData data) {
    return data.sess.login_user != null
            && data.sess.login_user.userid != null;
  }


	@Override
	public boolean needLogin() {
		return false;
	}

}
