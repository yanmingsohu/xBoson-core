////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-11 上午10:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/SystemStartupScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.IConstant;


public class SystemStartupScript extends Test implements IConstant {

  @Override
  public void test() throws Throwable {
    sub("System startup all script execution");

    TestApi.RunApi ra = new TestApi.RunApi(SYS_ORG, "26c0f25501d24c0993515d445e1215a5");

    msg("刷新本地DB连接信息");
//    ra.run("cacheinit", "localdbconn");
    msg("sql语句缓存");
    ra.run("cacheinit", "systemsqls");
    msg("系统配置属性信息");
    ra.run("cacheinit", "systemconfig");
    msg("租户信息缓存");
    ra.run("cacheinit", "systemtenants");
    msg("数据连接服务信息");
    ra.run("cacheinit", "systemdbconnection");

    msg("数据集完整信息");
    ra.run("cacheinit", "cachealldatasetinfo");
    msg("API缓存");
    ra.run("cacheinit", "systemapis");
    msg("平台数据字典");
//    ra.run("cacheinit", "platdict");
    msg("角色权限");
    ra.run("cacheinit", "rbac");
    msg("第三方应用信息");
    ra.run("cacheinit", "systemapps");

    msg("系统信息");
    ra.run("cacheinit", "systemsystems");
    msg("业务模型信息");
    ra.run("cacheinit", "model_info");
    msg("页面信息");
    ra.run("cacheinit", "cachepages");
  }


  public static void main(String[] a) {
    new SystemStartupScript();
  }
}
