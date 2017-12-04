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

import com.xboson.been.ApiCall;
import com.xboson.been.UrlSplit;
import com.xboson.script.JScriptException;


/**
 * 作为全局脚本的入口, 维护所有运行着的沙箱/应用池
 */
public class AppFactory {

  private static AppFactory instance;
  private AppPool app_pool;


  private AppFactory() {
    app_pool = new AppPool();
  }


  public void call(ApiCall ac) {
    try {
      XjOrg org = app_pool.getOrg(ac.org);
      XjApp app = org.getApp(ac.app);
      app.run(ac.call, ac.mod, ac.api);
    } catch (JScriptException jse) {
      throw jse;
    } catch (Exception e) {
      throw new JScriptException(e);
    }
  }


  /**
   * 分析 url 参数, 并将请求映射到 api 上, 返回的对象中 call 属性为 null.
   * @param url 该参数是安全的, 不会被改变.
   */
  public ApiCall parse(UrlSplit url) {
    ApiCall ret = new ApiCall();
    UrlSplit sp = url.clone();
    sp.withoutSlash(true);

    ret.org = sp.getName();
    ret.app = sp.next();
    ret.mod = sp.next();
    ret.api = sp.next();
    return ret;
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
}
