////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-22 下午2:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestApi.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.app.AppPool;
import com.xboson.app.XjApp;
import com.xboson.app.XjOrg;
import com.xboson.been.CallData;
import com.xboson.been.LoginUser;
import com.xboson.been.SessionData;
import com.xboson.j2ee.container.XResponse;
import com.xboson.test.impl.TestServletRequest;
import com.xboson.test.impl.TestServletResponse;

import java.io.IOException;
import java.nio.ByteBuffer;


public class TestApi extends Test {

  public void test() throws Exception {
    test_pool();
  }


  public void test_ds_api(XjApp app, CallData cd) throws Exception {
    sub("Test sys api");
    String path = "/test_double/test-sys";
    try {
      app.run(cd, path);
    } catch(Exception e) {
      show_code(app, path);
      fail(e);
      e.printStackTrace();
    }
  }


  public void test_pool() throws Exception {
    sub("Test app pool");

    CallData cd = simulationCallData();

    AppPool ap = new AppPool();
    XjOrg org = ap.getOrg("a297dfacd7a84eab9656675f61750078");
    XjApp app = org.getApp("a9943b0fb1e141b3a3ce7e886d407f5b");

    test_ds_api(app, cd);
  }


  public static CallData simulationCallData() throws Exception {
    TestServletRequest req = new TestServletRequest();
    TestServletResponse resp = new TestServletResponse();
    XResponse xr = new XResponse(req, resp);
    SessionData sd = new SessionData();
    sd.login_user = new LoginUser();
    sd.login_user.pid = "e3e5cf168dd24b44ba4b72775d5fb215";
    sd.login_user.userid = "root";
    req.setAttribute(SessionData.attrname, sd);
    return new CallData(req, resp);
  }


  public void show_code(XjApp app, String path) throws IOException {
    ByteBuffer buf = app.readFile(path);
    String code = new String(buf.array());
    printCode(code);
  }


  public static void main(String[] a) {
    new TestApi();
  }

}
