////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-4 上午8:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/AppFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.auth.IAWho;
import com.xboson.been.ApiCall;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.JScriptException;
import com.xboson.util.IConstant;

import java.util.HashMap;
import java.util.Map;


/**
 * 作为全局脚本的入口, 维护所有运行着的沙箱/应用池
 */
public class AppFactory implements IConstant {

  private static AppFactory instance;
  private AppPool app_pool;
  private ThreadLocal<ThreadLocalData> ttld;
  private Log log;


  private AppFactory() {
    log       = LogFactory.create();
    app_pool  = new AppPool();
    ttld      = new ThreadLocal<>();
  }


  public void call(ApiCall ac) {
    try {
      ThreadLocalData tld = new ThreadLocalData();
      ttld.set(tld);
      tld.who = ac.call.sess.login_user;

      log.debug("Call::", ac.org, '/', ac.app, '/', ac.mod, '/', ac.api);

      make_extend_parameter(ac);
      tld.orgid = ac.org;

      //
      // 利用 app 名称判断是否调用平台机构 api, 此时 org 可以是另一个机构, 这种跨机构
      // 调用 api 的行为只支持平台机构; 此时 api 以 root 的数据库权限启动, 而参数中
      // 的 org 仍然是原先机构的 id.
      //
      // 即使 org 是其他机构, 运行的仍然是平台机构中的 api, 所以不会有代码越权访问资源,
      // 必须保证平台机构 api 逻辑安全.
      //
      if (ac.app.startsWith(SYS_APP_PREFIX)) {
        ac.org = SYS_ORG;
        tld.replaceOrg = true;
      }

      XjOrg org = app_pool.getOrg(ac.org);
      XjApp app = org.getApp(ac.app);
      app.run(ac.call, ac.mod, ac.api);

    } catch (XBosonException x) {
      throw x;

    } catch (Exception e) {
      throw new XBosonException(e);

    } finally {
      ttld.set(null);
    }
  }


  private void make_extend_parameter(ApiCall ac) {
    Map<String, String> ex = new HashMap<>();
    ttld.get().exparam = ex;
    ex.put("org", ac.org);
    ex.put("app", ac.app);
    ex.put("mod", ac.mod);
  }


  /**
   * 返回扩展请求参数
   * @return
   */
  public Map<String, String> getExtendParameter() {
    return ttld.get().exparam;
  }


  /**
   * 返回当前请求的 org-id, 当前 api 可能在另一个机构中.
   */
  public String currendOrg() {
    return ttld.get().orgid;
  }


  /**
   * 分析 url 参数, 并将请求映射到 api 上, 返回的对象中 call 属性为 null.
   * @param url 该参数是安全的, 不会被改变.
   */
  public ApiCall parse(UrlSplit url) {
    ApiCall ret = new ApiCall();
    UrlSplit sp = url.clone();
    sp.withoutSlash(true);

    ret.org = sp.next();
    ret.app = sp.next();
    ret.mod = sp.next();
    ret.api = sp.next();
    return ret;
  }


  /**
   * 在任何位置都可以安全调用该方法, 返回当前登录的用户,
   * 如果没有用户登录会抛出异常.
   */
  public IAWho who() {
    IAWho r = ttld.get().who;
    if (r == null) {
      throw new XBosonException("not login");
    }
    return r;
  }


  /**
   * 当 org 被替换后, 返回 ture.
   * 替换的 org
   */
  public boolean isReplaceOrg() {
    return ttld.get().replaceOrg;
  }


  public static AppFactory me() {
    if (instance == null) {
      synchronized (AppFactory.class) {
        if (instance == null) {
          instance = new AppFactory();
        }
      }
    }
    return instance;
  }


  private class ThreadLocalData {
    Map<String, String> exparam;
    IAWho who;
    String orgid;
    boolean replaceOrg;
  }
}
