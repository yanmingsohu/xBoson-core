////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 上午10:38:36
// 原始文件路径: xBoson/src/com/xboson/service/App.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.app.AppFactory;
import com.xboson.app.AppPool;
import com.xboson.app.XjApp;
import com.xboson.app.XjOrg;
import com.xboson.been.ApiCall;
import com.xboson.been.CallData;
import com.xboson.been.UrlSplit;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;

import javax.servlet.ServletException;
import java.io.IOException;


@XPath("/app")
public class App extends XService {

  private static final String PATH_FOTMAT
          = "Path format: /app/{org id}/{app id}/{module id}/{api name}";

  private AppPool app_pool;


  public App() {
    app_pool = new AppPool();
  }


  @Override
  public void service(CallData data) throws ServletException, IOException {
    AppFactory af = AppFactory.me();
    data.url.setErrorMessage(PATH_FOTMAT);

    ApiCall ac = af.parse(data.url);
    ac.call = data;

    af.call(ac);
  }

}
