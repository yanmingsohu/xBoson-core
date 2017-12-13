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

package com.xboson.app;

import com.xboson.been.ApiCall;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.test.TestApi;
import com.xboson.util.IConstant;
import com.xboson.util.Tool;


/**
 * 这里应该调用系统启动脚本, 在单独的线程中.
 */
public class SystemStartupScript implements IConstant, Runnable {

  private Log log;
  private static boolean is_init = false;


  public static void me() {
    if (is_init) return;
    is_init = true;
    SystemStartupScript sss = new SystemStartupScript();
    sss.start();
  }


  private SystemStartupScript() {
    log = LogFactory.create();
  }


  /**
   * 启动线程进行初始化操作
   */
  public void start() {
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }


  @Override
  public void run() {
    try {
      log.info("缓存初始化启动..");

      ApiCall ac = new ApiCall();
      ac.org = SYS_ORG;
      ac.app = "26c0f25501d24c0993515d445e1215a5";
      ac.mod = "cacheinit";
      ac.api = "total";
      ac.call = TestApi.simulationCallData();

      AppContext.me().call(ac);
      ac.call.resp.flushBuffer();

      log.info("缓存初始化完成.");
    } catch (Exception e) {
      log.info("缓存初始化错误", Tool.allStack(e));
    }
  }
}
