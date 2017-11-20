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

import com.xboson.been.Config;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.j2ee.html.HtmlBuilder;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;


/**
 * 当操作成功返回 http 状态 200, 文件找不到返回 404,
 * 操作失败返回 500 并且设置 http 头域 Error-Message 包含错误消息字符串.
 */
public class UIEngineServlet extends HttpServlet {

  public static final String MY_URL = "/face";
  public static final String MIME_FILE = "./mime-types.properties";

  private IUIFileProvider file_provider;
  private Log log;
  private FileTypeMap mime;
  private String baseurl;
  /** 当该配置为 true, 用户打开的路径是目录则返回目录内文件列表 */
  private boolean list_dir;


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.log = LogFactory.create();
    this.mime = new MimetypesFileTypeMap(getClass().getResourceAsStream(MIME_FILE));
    this.baseurl = config.getServletContext().getContextPath() + MY_URL;

    try {
      Config cf = SysConfig.me().readConfig();
      this.list_dir = cf.uiListDir;

      String providerClass = cf.uiProviderClass;
      Class cl = Class.forName(providerClass);
      file_provider = (IUIFileProvider) cl.newInstance();
    } catch(Exception e) {
      throw new ServletException(e);
    }
  }


  /**
   * 返回文件路径
   * @param req
   * @return 请求文件的路径, 已经规范化
   * @throws IOException
   */
  private String getReqFile(HttpServletRequest req)
          throws IOException {

    UrlSplit url = new UrlSplit(req);
    String last = url.getLast();
    if (last == null) {
      return null;
    }

    String path = Tool.normalize(last);
    if (path.equals("/")) return null;
    return path;
  }


  /**
   * 读取文件
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    String path = getReqFile(req);
    if (path == null) {
      if (!list_dir) {
        resp.sendError(400,
                "No file request (e.g: /face/ui/paas/login.html)");
        return;
      }
      path = "/";
    }

    try {
      String file_type = mime.getContentType(path);
      resp.setContentType(file_type);

      byte[] content = file_provider.readFile(path);
      resp.getOutputStream().write(content);
      log.debug("Get", file_type, path);

    } catch(NoSuchFileException nofile) {
      log.debug(nofile);
      resp.sendError(404, path);

    } catch(AccessDeniedException access) {
      log.debug(access);
      resp.sendError(406, path);

    } catch(XBosonException.ISDirectory dir) {
      log.debug(dir);

      if (list_dir) {
        resp.setContentType("text/html");
        resp.setHeader("Cache-Control", "no-cache");
        HtmlBuilder.listDir(resp.getWriter(), dir.getPath(), baseurl + path);
      } else {
        resp.sendError(403, path);
      }
    }
  }


  /**
   * 写入文件
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("POST file ", url.getLast());
    throw new UnsupportedOperationException(); // !!!!!!!!!!!!!!!!!!!!
  }


  /**
   * 删除文件
   */
  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("DELETE file ", url.getLast());
    throw new UnsupportedOperationException(); // !!!!!!!!!!!!!!!!!!!!
  }


  @Override
  protected long getLastModified(HttpServletRequest req) {
    try {
      String path = getReqFile(req);
      if (path == null)
        return -1;

      log.debug("Last Modified", path);
      return file_provider.modifyTime(path);

    } catch (Exception e) {
      log.error(e);
      return -1;
    }
  }
}
