/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
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
import com.xboson.been.XBosonException;
import com.xboson.db.DbmsFactory;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.init.install.step.*;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.log.writer.ConsoleOut;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.SysConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Install extends HttpServlet {

  public static final String PAGE_PATH = "/WEB-INF/install-page/";
  public static final String NULSTR = IConstant.NULL_STR;


  /**
   * 按顺序执行初始化过程
   */
  private static final Class[] ALL_STEP = new Class[] {
          Welcome.class,
          Copyright.class,
          RegLicense.class,
          RootUser.class,
          ConfigCluster.class,
          ConfigCoreDB.class,
          ConfigRedis.class,
          ConfigLog.class,
          UiConfig.class,
          NodeModuleConfig.class,
          ConfigMongoDB.class,
          ConfigShell.class,
          SaveConfig.class,
          RestartServer.class,
  };


  private List<IStep> steps;
  private Config config;
  private int step = 0;
  private Log log;


  public Install() {
    this.config = SysConfig.me().readConfig();
    this.steps  = new ArrayList<>();
    this.log    = LogFactory.create();

    LogFactory.me().setWriter(new ConsoleOut());
    DbmsFactory.me().registeringDefaultDriver();

    try {
      for (Class c : ALL_STEP) {
        steps.add((IStep) c.newInstance());
      }
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    req.setCharacterEncoding("utf8");
    resp.setCharacterEncoding("utf8");

    HttpData hd = new HttpData(req, resp, config);
    String next = req.getParameter("next");
    IStep is = steps.get(step);

    if (next != null) {
      try {
        if (is.gotoNext(hd)) {
          ++step;
        }
      } catch(NullPointerException e) {
        hd.msg = "空值异常";
        e.printStackTrace();
      } catch(Exception e) {
        hd.msg = e.getMessage();
      }
    }

    if (hd.reset) {
      step = 0;
    }

    is = steps.get(step);
    if (is == null) step = 0;

    String page = is.getPage(hd);
    String msg = hd.msg == null ? NULSTR : hd.msg;
    log.info("Run install Step:", is.getClass(), "#", msg);

    if (page == null) {
      page = "_building_.jsp";
    }

    //
    // 页面通过变量传递配置
    //
    req.setAttribute("config", config);
    req.setAttribute("msg", msg);

    if (hd.ajax) {
      resp.getWriter().write(msg);
    } else {
      req.getRequestDispatcher(PAGE_PATH + page).forward(req, resp);
    }
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    doGet(req, resp);
  }


  /**
   * 将地址重映射到安装步骤上
   */
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
