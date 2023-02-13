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
// 文件创建日期: 2017年11月2日 下午1:02:16
// 原始文件路径: xBoson/src/com/xboson/j2ee/container/Striker.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import com.xboson.app.ErrorCodeMessage;
import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;


/**
 * 抓住所有异常, 编码转换, 等初始化操作;
 * 该对象持有线程级 jee 相关变量.
 *
 * @see com.xboson.init.Startup 配置到容器
 */
public final class Striker extends HttpFilter {

  private Processes.Happy birth;
  private Log log;
  private boolean debug;
  private String context_path;
  private String welcome;

  // 优化路径比较算法
  private int check1hash;
  private int check1len;
  private int check2hash;
  private int check2len;

  private static ThreadLocal<JeeContext> tl = new ThreadLocal<>();


  /**
   * 该方法返回当前线程 servlet 请求的相关变量, 如果当前线程不是 jee 请求抛出异常.
   */
  public static JeeContext jee() {
    JeeContext j = tl.get();
    if (j == null) {
      throw new XBosonException("Not in JEE request");
    }
    return j;
  }


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    super.init(filterConfig);
    Config cf = SysConfig.me().readConfig();

    this.birth        = Processes.me().new Happy();
    this.log          = LogFactory.create("striker");
    this.debug        = cf.debugService;
    this.context_path = filterConfig.getServletContext().getContextPath();
    this.check1hash   = context_path.hashCode();
    this.check2hash   = (context_path + '/').hashCode();
    this.check1len    = context_path.length();
    this.check2len    = check1len + 1;

    if (cf.uiWelcome != null) {
      this.welcome = Tool.normalize(context_path +'/'+ cf.uiWelcome);
      log.info("Welcome page:", this.welcome);
    }
  }


  protected void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain chain)
          throws IOException, ServletException {

    response.setCharacterEncoding("utf8");
    request.setCharacterEncoding("utf8");
    XResponse jr = null;

    if (welcome != null) {
      final String uri  = request.getRequestURI();
      final int uri_len = uri.length();
      //
      // 不使用字符串的比较, 而是认为相同长度且hash相同的字符串相同.
      // 虽然 hash 容易产生冲突, 但是 uri 的前缀一致由容器保证,
      // 后缀一致由长度保证. 这种优化方法仅在这个场景可用 !
      //
      if ( (uri_len == check1len && uri.hashCode() == check1hash) ||
           (uri_len == check2len && uri.hashCode() == check2hash) )
      {
        response.sendRedirect(welcome);
        return;
      }
    }

    try {
      birth.lock();
      tl.set(new JeeContext(request, response));
      jr = new XResponse(request, response);
      chain.doFilter(request, response);

    } catch(Throwable e) {
      responseError(e, jr, response);
    } finally {
      tl.remove();
      birth.unlock();
    }
  }


  private void responseError(Throwable e, XResponse jr, HttpServletResponse response) {
    log.error("ResponseError", Tool.allStack(e));
    if (response.isCommitted()) {
      return;
    }

    //
    // 初始化失败的情况
    //
    if (jr == null) {
      try {
        Writer out = response.getWriter();
        out.write("{ \"code\": 999, \"msg\": \"System Fail: ");
        out.write(e.getMessage());
        out.write("\" }");
        log.error("XResponse is null", e);
      } catch (IOException e1) {
        log.debug("XResponse is null, open writer fail", e1);
      }
      return;
    }

    if (debug) {
      showStack(e, jr);
    } else {
      showMessage(e, jr);
    }

    findCode(e, jr);


    try {
      //
      // 如果抛出 IllegalStateException, 这是编程错误, 必须找出原因而不是抓住异常 !
      //
      jr.response();
    } catch (IOException e1) {
      //
      // 当客户端已经断开连接, 可能抛出错误, 这个错误并不重要.
      //
      log.debug("XResponse response fail", e1);
    }
  }


  private void findCode(Throwable e, XResponse jr) {
    do {
      if (e instanceof XBosonException) {
        XBosonException xe = (XBosonException) e;
        int code = xe.getCode();
        String msg = ErrorCodeMessage.getNul(code);
        if (msg == null) msg = e.getMessage();
        jr.setMessage(msg, code);
        break;
      }
      e = e.getCause();
    } while(e != null);
  }


  private void showMessage(Throwable e, XResponse jr) {
    String msg = e.getMessage();
    if (msg == null) {
      msg = e.toString();
    }
    jr.setData(msg);
  }


  private void showStack(Throwable e, XResponse jr) {
    StringBuilder out = new StringBuilder();
    Tool.xbosonStack(e, out);
    String msg = out.toString();
    jr.setData(msg, StackTraceElement.class);
  }


  public class JeeContext {
    public final ServletContext servletContext;
    public final HttpServletRequest request;
    public final HttpServletResponse response;

    private JeeContext(HttpServletRequest req, HttpServletResponse resp) {
      this.request = req;
      this.response = resp;
      this.servletContext = req.getServletContext();
    }
  }
}
