////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-1-19 下午12:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/Independent.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init;

import com.xboson.been.AppSelf;
import com.xboson.event.OnExitHandle;
import com.xboson.init.install.step.RestartServer;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.NullSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 作为独立程序启动, 无需部署到 servlet 容器中.
 * TODO: gradle 任务需要能正确导出这个类, 以便生成启动脚本
 */
public class Independent extends OnExitHandle {

  private Server server;
  private Log log;


  public static void main(String[] av) throws Exception {
    RestartServer.autoRestart = true;
    Independent i = new Independent();
    i.startServer();
    i.startServerOnNewProcess(av);
    i.log("<<<<<<<<<<< Java VM Shutdown ... >>>>>>>>>>>");
  }


  private void startServer() throws Exception {
    AppSelf app = SysConfig.me().reloading().appSelf;
    if (app == null) app = new AppSelf();
    String cp = app.contextPath;

    ServletContextHandler h_servlet =
            new ServletContextHandler(null, cp, true, false);

    h_servlet.addEventListener(new Startup());
    h_servlet.setResourceBase("./WebRoot");
    h_servlet.setClassLoader(getClass().getClassLoader()); // 不安全?

    //
    // 使 session 完全不可用, 提升性能
    //
    SessionHandler sessions = h_servlet.getSessionHandler();
    SessionCache cache = new NullSessionCache(sessions);
    cache.setSessionDataStore(new NullSessionDataStore());
    sessions.setSessionCache(cache);

    //
    // JSP 相关初始化
    //
    JettyJasperInitializer jsp = new JettyJasperInitializer();
    h_servlet.addServlet(JettyJspServlet.class, "/");
    log = LogFactory.create("independent-server");

    //
    // 服务器启动, 函数顺序严格要求
    //
    server = new Server(app.httpPort);
    server.setHandler(h_servlet);
    server.start();
    jsp.onStartup(null, h_servlet.getServletContext());
    log("see", "http://localhost:"+ app.httpPort + cp);
    server.join();
  }


  /**
   * 复制自身的命令行代码, 并启动一个克隆进程
   */
  private void startServerOnNewProcess(String[] av) {
    RuntimeMXBean run = ManagementFactory.getRuntimeMXBean();

    List<String> cmd = new ArrayList<>();
    cmd.add("java");
    cmd.addAll(run.getInputArguments());

    String classPath = run.getClassPath();
    if (! Tool.isNulStr(classPath)) {
      cmd.add("-classpath");
      cmd.add(run.getClassPath());
    }

    // main function in class
    cmd.add(this.getClass().getName());

    if (av != null && av.length > 0) {
      cmd.addAll(Arrays.asList(av));
    }

    ProcessBuilder pb = new ProcessBuilder(cmd);
    try {
      pb.inheritIO();
      pb.start();
      log("Start process success:\n", cmd);
    } catch (Exception e) {
      log("Start process fail:", e.getMessage());
    }
  }


  @Override
  protected void exit() {
    if (server != null) {
      server.setStopTimeout(5000);
      while (! server.isStopped()) {
        try {
          server.stop();
          log = null;
        } catch (Exception e) {
          log("Stop http server", e);
        }
      }
      log("Http server exit");
    }
  }


  private void log(Object ...o) {
    if (log != null) {
      log.info(o);
    } else {
      StringBuilder s = new StringBuilder();
      Tool.pl("- NO LOG", "[independent-server]", Tool.join(s, o));
    }
  }
}
