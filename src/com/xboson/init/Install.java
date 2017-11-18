////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-16 下午8:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/Install.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init;

import com.xboson.been.Config;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.log.LogFactory;
import com.xboson.util.Password;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import static com.xboson.init.Startup.INIT_FILE;


public class Install extends HttpServlet {

  private static final String PAGE_PATH = "/WEB-INF/install-page/";

  private Config config;
  private int step = 0;


  public Install() {
    config = SysConfig.me().readConfig();
    LogFactory.me().setType("ConsoleOut");
    DbmsFactory.me().registeringDefaultDriver();
  }


  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    String page = null;
    req.setCharacterEncoding("utf8");
    resp.setCharacterEncoding("utf8");
    ServletContext sc = req.getServletContext();
    String msg = "";

    String next = req.getParameter("next");
    //
    // 请求进入下一个步骤, 验证参数
    //
    if (next != null) {
      switch (step) {
        case 0:
          if (req.getParameter("begin_config") != null) {
            step = 1;
          }
          break;

        case 1:
          String copyright = req.getParameter("copyright");
          if ("yes".equals(copyright)) {
            step = 2;
          } else {
            msg = "您必须接受版权条款方可继续";
          }
          break;

        case 2:
          String un = req.getParameter("rootUserName");
          String up = req.getParameter("rootPassword");
          String p2 = req.getParameter("again");
          if (un == null || un.length()<4) {
            msg = "用户名不能小于4个字符";
          } else if (up == null || up.length()<6) {
            msg = "密码不能小于6个字符";
          } else if (!up.equals(p2)) {
            msg = "重复密码错误";
          } else {
            config.rootUserName = un;
            config.rootPassword = Password.v1(un, Password.md5(up));
            step = 3;
          }
          break;

        case 3:
          config.db.setHost(req.getParameter("host"));
          config.db.setPort(req.getParameter("port"));
          config.db.setDbname(req.getParameter("dbname"));
          config.db.setUsername(req.getParameter("username"));
          config.db.setPassword(req.getParameter("password"));
          config.db.setDatabase(req.getParameter("database"));

          try (Connection conn = DbmsFactory.me().openWithoutPool(config.db)) {
            step = 4;
          } catch(Exception e) {
            msg = e.getMessage();
          }
          break;

        case 4:
          ConnectConfig redis = config.redis;
          redis.setHost(req.getParameter("rhost"));
          redis.setPort(req.getParameter("rport"));
          redis.setPassword(req.getParameter("rpassword"));

          try (Jedis jc = new Jedis(redis.getHost(),
                  redis.getIntPort(6379))) {
            if (!Tool.isNulStr(redis.getPassword())) {
              jc.auth(redis.getPassword());
            }
            jc.ping();
            step = 99;
          } catch(Exception e) {
            msg = e.getMessage();
            e.printStackTrace();
          }
          break;

        case 99:
          String cjson = Tool.getAdapter(Config.class).toJson(config);
          req.setAttribute("configstr", cjson);
          String act = req.getParameter("act");

          if ("reconfig".equals(act)) {
            step = 0;
          }
          else if ("restart".equals(act)) {
            File init_file = new File(config.configPath + INIT_FILE);
            FileWriter w = new FileWriter(init_file);
            w.write(new Date().toString());
            w.close();

            SysConfig.me().generateDefaultConfigFile(config);
            msg = "系统即将重启...";
            step = 100;
          }
          else {
            msg = "请选择一个操作";
          }
          break;

        case 100:
          if (!restart_server(req, resp)) {
            msg = "您需要手动重启 Servlet 容器..";
          }
          break;
      }
    }

    //
    // 检查当前安装步骤, 然后返回对应的页面
    //
    switch(step) {
      default:
        page = "_building_.jsp";
        break;

      case 0:
        req.setAttribute("j2ee_info", sc.getServerInfo());
        page = "welcome.jsp";
        break;

      case 1:
        page = "copyright.jsp";
        break;

      case 2:
        page = "root.jsp";
        break;

      case 3:
        page = "db.jsp";
        break;

      case 4:
        page = "redis.jsp";
        break;

      case 99:
        page = "success.jsp";
        break;

      case 100:
        if (!restart_server(req, resp)) {
          msg = "您需要手动重启 Servlet 容器..";
        }
        page = "restarting.jsp";
        break;
    }


    //
    // 页面通过变量传递配置
    //
    req.setAttribute("config", config);
    req.setAttribute("msg", msg);
    req.getRequestDispatcher(PAGE_PATH + page).forward(req, resp);
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    doGet(req, resp);
  }


  private boolean restart_server(HttpServletRequest req, HttpServletResponse resp) {
    return false;
  }


  static public class InstallFilter extends HttpFilter {
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

      String uri = request.getRequestURI();

      if (uri.indexOf("/install/api") >= 0) {
        chain.doFilter(request, response);
      } else {
        response.sendRedirect(
                request.getContextPath() + "/install/api");
      }
    }
  }
}
