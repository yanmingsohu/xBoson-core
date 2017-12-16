////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-4 上午8:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/AppContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.reader.ForDevelopment;
import com.xboson.app.reader.ForProduction;
import com.xboson.auth.IAWho;
import com.xboson.been.ApiCall;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.IConstant;

import java.util.HashMap;
import java.util.Map;


/**
 * 作为全局脚本的入口, 维护所有运行着的沙箱/应用池
 */
public class AppContext implements IConstant {

  private static AppContext instance;
  private AppPool production;
  private AppPool development;
  private ThreadLocal<ThreadLocalData> ttld;
  private Log log;


  private AppContext() {
    log         = LogFactory.create();
    ttld        = new ThreadLocal<>();
    production  = new AppPool(new ForProduction());
    development = new AppPool(new ForDevelopment());
  }


  /**
   * 该方法支持嵌套请求, 前一个请求的参数会被保留在 ThreadLocalData.nestedCall 中.
   */
  public void call(ApiCall ac) {
    log.debug("Call::", ApiPath.getPath(ac));

    try {
      ThreadLocalData tld = createLocalData(ac);
      make_extend_parameter(ac);

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

      XjOrg org = chooseAppPool(tld).getOrg(ac.org);
      XjApp app = org.getApp(ac.app);
      app.run(ac.call, ac.mod, ac.api);

    } catch (XBosonException x) {
      throw x;

    } catch (Exception e) {
      throw new XBosonException(e);

    } finally {
      ThreadLocalData current = ttld.get();
      ttld.set(current.nestedCall);
    }
  }


  /**
   * 's' 调试状态标记
   *    d：执行最新代码并返回调试信息，
   *    r：执行已发布代码并返回调试信息
   */
  private AppPool chooseAppPool(ThreadLocalData tld) {
    ApiTypes type = ApiTypes.of( tld.ac.call.req.getParameter("s") );
    tld.__dev_mode = type;

    switch (type) {
      case Development:
        return development;

      case Production:
        return production;
    }
    throw new XBosonException.NotExist("Unknow type " + type);
  }


  private ThreadLocalData createLocalData(ApiCall ac) {
    ThreadLocalData tld = new ThreadLocalData();
    tld.who = ac.call.sess.login_user;
    tld.orgid = ac.org;
    tld.ac = ac;

    ThreadLocalData previous = ttld.get();
    if (previous != null) {
      tld.nestedCall = previous;
    }
    ttld.set(tld);
    return tld;
  }


  private void make_extend_parameter(ApiCall ac) {
    Map<String, Object> ex = ac.exparam;
    if (ex == null) {
      ex = new HashMap<>();
    }
    ttld.get().exparam = ex;
    ex.put("org", ac.org);
    ex.put("app", ac.app);
    ex.put("mod", ac.mod);
  }


  /**
   * 返回扩展请求参数
   * @return
   */
  public Map<String, Object> getExtendParameter() {
    return ttld.get().exparam;
  }


  /**
   * 返回当前请求的 org-id, 当前 api 可能在另一个机构中.
   */
  public String originalOrg() {
    return ttld.get().orgid;
  }


  /**
   * 返回当前 api 的抽象文件路径
   */
  public String getCurrentApiPath() {
    return ttld.get().getApiPath();
  }


  /**
   * 返回 api 类型
   */
  public ApiTypes getApiModeType() {
    return ttld.get().__dev_mode;
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
   * 替换的 org 通过 originalOrg() 可以得到, 只在 sys 机构时发生, 用于 sys 机构中的
   * api 访问其他机构中的表.
   */
  public boolean isReplaceOrg() {
    return ttld.get().replaceOrg;
  }


  public static AppContext me() {
    if (instance == null) {
      synchronized (AppContext.class) {
        if (instance == null) {
          instance = new AppContext();
        }
      }
    }
    return instance;
  }


  /**
   * 线程变量保存包装器
   */
  private class ThreadLocalData {
    ThreadLocalData nestedCall;
    Map<String, Object> exparam;
    IAWho who;
    String orgid;
    ApiCall ac;
    boolean replaceOrg;

    String __cache_path;
    ApiTypes __dev_mode;


    /**
     * 返回未被替换的原始参数.
     */
    String getApiPath() {
      if (__cache_path == null) {
        __cache_path = ApiPath.getPath(exparam, ac.api);
      }
      return __cache_path;
    }
  }
}
