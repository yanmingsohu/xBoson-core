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
import com.xboson.been.Config;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Level;
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
import org.eclipse.jetty.util.resource.Resource;
import sun.misc.Signal;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 作为独立程序启动, 无需部署到 servlet 容器中.
 */
public class Independent extends OnExitHandle {

  private Server server;
  private Log log;


  public static void main(String[] av) throws Exception {
    SystemFlag.canRestart = true;
    Independent i = new Independent();
    i.startServer();
    if (SystemFlag.autoRestart) {
      i.startServerOnNewProcess(av);
    }
    i.log("<<<<<<<<<<< Java VM Shutdown ... >>>>>>>>>>>");
  }


  private void startServer() throws Exception {
    Config config = SysConfig.me().readConfig();
    AppSelf app   = config.appSelf != null ? config.appSelf : new AppSelf();
    String cp     = app.contextPath;

    if (! Level.find(config.logLevel).blocking(Level.DEBUG)) {
      org.eclipse.jetty.util.log.Log.getLog().setDebugEnabled(true);
    }

    ServletContextHandler h_servlet =
            new ServletContextHandler(null, cp, true, false);

    h_servlet.addEventListener(new Startup());
    h_servlet.setClassLoader(getClass().getClassLoader()); // 不安全?
    h_servlet.setMaxFormContentSize(Integer.MAX_VALUE);
    h_servlet.setMaxFormKeys(Integer.MAX_VALUE);

    if (Tool.isInJar) {
      //
      // jar 独立运行包资源加载器
      //
      h_servlet.setBaseResource(getRootResourceFromJar());
    } else {
      //
      // 开发模式带有 WebRoot 目录
      //
      h_servlet.setResourceBase("./WebRoot");
    }

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
    hookTermSignal();
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


  /**
   * 获取 jar 根路径的方法似乎可以优化
   */
  private Resource getRootResourceFromJar() throws MalformedURLException {
    URL url = Independent.class.getResource("");
    String path = url.toString();

    int find = path.indexOf("!/");
    if (find < 0) {
      throw new MalformedURLException("Invalid JAR URL "+ path);
    }
    path = path.substring(0, find+2);
    url = new URL(path);
    return Resource.newResource(url);
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


  private void hookTermSignal() {
    Signal.handle(new Signal("TERM"), (name)->{
      log.info("Got signal", name);
      if (server != null) {
        try {
          server.stop();
        } catch(Exception e) {
          log.error(e);
        }
      } else {
        log.warn("server not started");
      }
    });
  }
}
