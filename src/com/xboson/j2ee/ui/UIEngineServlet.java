////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/UIEngineServlet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.been.UrlSplit;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UIEngineServlet extends HttpServlet {

  private IUIFileProvider file_provider;
  private Log log;


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log = LogFactory.create();
    String providerClass = SysConfig.me().readConfig().uiProviderClass;

    try {
      Class cl = Class.forName(providerClass);
      file_provider = (IUIFileProvider) cl.newInstance();
    } catch(Exception e) {
      throw new ServletException(e);
    }
  }


  /**
   * 读取文件
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("GET file ", url.getLast());
  }


  /**
   * 写入文件
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("POST file ", url.getLast());
  }


  /**
   * 删除文件
   */
  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("DELETE file ", url.getLast());
  }
}
