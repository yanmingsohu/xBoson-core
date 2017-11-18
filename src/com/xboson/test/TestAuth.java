////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 上午8:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestAuth.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.auth.*;
import com.xboson.db.ConnectConfig;

import java.net.URI;
import java.net.URISyntaxException;


public class TestAuth extends Test {

  private ConnectConfig cc;

  public void test() throws Throwable {
    cc = TestDS.connect_config();
    framework();
  }


  public void framework() throws Exception {
    AuthFactory af = AuthFactory.me();
    af.initWhoContext(null);
    ILoginContext context = af.whoContext();
    Who who = new Who("1");
    context.login(who);

    Res res = new Res("1");
    PermissionSystem.apply(Where.class, res);
    msg(res.toURI() + " pass");

    new Throws(PermissionException.class) {
      public void run() throws Throwable {
        Res res2 = new Res("2");
        PermissionSystem.apply(Where.class, res2);
      }
    };

    context.logout(who);
    af.outWhoContext(null);
  }


  public static void main(String[] a) {
    new TestAuth();
  }


  static public class Who implements IAWho {
    private String id;
    Who(String id) { this.id = id; }
    public String identification() {
      return "/" + id;
    }
  }


  static public class Res implements IAResource {
    private String id;
    Res(String id) { this.id = id; }
    public URI toURI() {
      try {
        return new URI("auth://test/" + id);
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
      return null;
    }
  }


  static public class Where implements IAWhere {
    public boolean apply(IAWho who, IAResource res) {
      return res.toURI().getPath().equals( who.identification() );
    }
  }

}
