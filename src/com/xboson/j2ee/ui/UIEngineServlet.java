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
// 文件创建日期: 17-11-18 下午8:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/UIEngineServlet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.squareup.moshi.JsonAdapter;
import com.xboson.been.Config;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.redis.RedisFileAttr;
import com.xboson.fs.ui.UIFileFactory;
import com.xboson.j2ee.container.IHttpHeader;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Path;
import com.xboson.util.JsonLite;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.Set;


/**
 * 当操作成功返回 http 状态 200, 文件找不到返回 404,
 * 操作失败返回 500 并且设置 http 头域 Error-Message 包含错误消息字符串.
 *
 * 应用路径: /[xboson]/face/*[文件目录]/**
 *
 * @see com.xboson.init.Startup 配置到容器
 */
public class UIEngineServlet extends HttpServlet implements IHttpHeader {

  public static final String MY_URL = "/face";
  public static final String HTML_TYPE = CONTENT_TYPE_HTML;
  public static final String H_FULL_PATH = "Full-Path";
  public static final int    MAX_AGE = 30 * 60;
  /**
   * 缓存策略: 用户打开浏览器第一次访问的资源将被检查修改时间, 并返回内容或状态,
   * 此后 MAX_AGE 时间内都使用浏览器缓存中的资源, 并在 MAX_AGE 之后重新访问服务器获取状态;
   * 开发人员需要频繁修改/访问资源, 应该开启浏览器调试模式, 并启用浏览器的 'Disable-cache'.
   */
  public static final String CACHE_DIRECTIVE =
          "must-revalidation, max-age="+ MAX_AGE;

  private IRedisFileSystemProvider file_provider;
  private UIExtRenderService ext_render;
  private TemplateEngine template;
  private Log log;
  private FileTypeMap mime;
  private String baseurl;

  /** 当该配置为 true, 用户打开的路径是目录则返回目录内文件列表 */
  private boolean list_dir;


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.log = LogFactory.create("ui-engine");
    this.mime = MimeTypeFactory.getFileTypeMap();
    this.baseurl = config.getServletContext().getContextPath() + MY_URL;

    Config cf = SysConfig.me().readConfig();
    this.list_dir = cf.uiListDir;
    this.file_provider = UIFileFactory.open();
    this.template = new TemplateEngine(file_provider);
    this.ext_render = new UIExtRenderService(new FileLoader());
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
      RedisFileAttr fs = file_provider.readAttribute(path);
      resp.setHeader(H_FULL_PATH, path);
      if (fs == null) {
        resp.sendError(400, path);
        return;
      }

      if (fs.isFile()) {
        final String ext = Path.me.extname(path).toLowerCase();
        if (TemplateEngine.EXT.equals(ext)) {
          resp.setContentType(HTML_TYPE);
          template.service(req, resp);
          return;
        }

        file_provider.readFileContent(fs);
        if (ext_render.canRender(path)) {
          log.debug("Render File:", path);
          RenderCallback rc = new RenderCallback(req, resp);
          ext_render.render(path, fs.getFileContent(), rc, new RenderParm(req));
          return;
        }

        if (isBlockFileType(ext)) {
          resp.sendError(403, path);
          return;
        }

        resp.setHeader(HEAD_CACHE, CACHE_DIRECTIVE);

        String file_type = mime.getContentType(path);
        resp.setContentType(file_type);
        OutputStream out = resp.getOutputStream();
        out.write(fs.getFileContent());
        out.flush();

        log.debug("Get File:", file_type, path);
      }
      else if (fs.isDir() && list_dir) {
        resp.setContentType(HTML_TYPE);
        resp.setHeader(HEAD_CACHE, VAL_CACHE_NO);
        Set<RedisFileAttr> files = file_provider.readDir(fs.path);
        HtmlDirList.toHtml(resp.getWriter(), files, baseurl + path);
      }

    } catch(NoSuchFileException
            | FileNotFoundException
            | XBosonException.NotFound e) {
      log.debug(e);
      resp.sendError(404, path);

    } catch(AccessDeniedException access) {
      log.debug(access);
      resp.sendError(406, path);
    }
  }


  /**
   * 写入文件
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    throw new UnsupportedOperationException("POST");
  }


  /**
   * 删除文件
   */
  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    UrlSplit url = new UrlSplit(req);
    log.warn("DELETE file ", url.getLast());
    throw new UnsupportedOperationException();
  }


  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    String path = getReqFile(req);
    if (path == null) {
      resp.sendError(400, path);
      return;
    }
    RedisFileAttr fs = file_provider.readAttribute(path);
    if (fs == null) {
      resp.sendError(404, path);
      return;
    }
    String file_type = mime.getContentType(path);
    resp.setContentType(file_type);
    resp.setHeader(H_FULL_PATH, path);
    log.debug("Head", file_type, path);
  }


  @Override
  protected long getLastModified(HttpServletRequest req) {
    try {
      String path = getReqFile(req);
      if (path == null)
        return -1;
      if (path.endsWith(TemplateEngine.EXT))
        return -1;

      log.debug("Last Modified", path);
      return file_provider.modifyTime(path);

    } catch (Exception e) {
      log.error(e);
      return -1;
    }
  }


  /**
   * ext 总是小写的
   */
  private boolean isBlockFileType(String ext) {
    switch (ext) {
      case ".vue":
      case ".ts":
      case ".jsx":
      case ".tsx":
      case ".less":
        return true;
    }
    return false;
  }


  /**
   * 编译参数来自 head 中的开发模式
   */
  private class RenderParm implements UIExtRenderService.ISequenceParameters {
    private final static String DEV_HEAD = "x-development-mode";
    private final static String DEV_MOE = "debug";
    private final static String ES6_HEAD = "x-es6-support";
    private final JsonLite j;

    private RenderParm(HttpServletRequest req) {
      j = JsonLite.objectRoot();
      j.put("minified",
        !DEV_MOE.equalsIgnoreCase(req.getHeader(DEV_HEAD)) );
      j.put("es6",
        Boolean.parseBoolean(req.getHeader(ES6_HEAD)));
      j.end();
    }

    @Override
    public String get() {
      return j.toString();
    }
  }


  /**
   * 编译参数来自 url 请求参数
   */
  private class RenderParmWithReq implements UIExtRenderService.ISequenceParameters {
    Map<?,?> p;

    private RenderParmWithReq(HttpServletRequest req) {
      p = req.getParameterMap();
    }

    @Override
    public String get() {
      try {
        if (p != null) {
          // 直接用 getClass 会让 moshi 抛出 org.apache.catalina.util.ParameterMap
          // 转换不了的错误
          JsonAdapter json = Tool.getAdapter(Map.class);
          return json.toJson(p);
        }
      } catch(Exception e) {
        log.error(e.getMessage());
      }
      return "{}";
    }
  }


  private class FileLoader implements UIExtRenderService.IFileReader {

    @Override
    public byte[] readfile(String fullpath) throws IOException {
      RedisFileAttr fs = file_provider.readAttribute(fullpath);
      if (fs == null) {
        throw new IOException("file not exists: "+ fullpath);
      }
      file_provider.readFileContent(fs);
      return fs.getFileContent();
    }
  }


  private class RenderCallback implements UIExtRenderService.IRenderFile {
    HttpServletRequest req;
    HttpServletResponse resp;
    AsyncContext ac;
    boolean useAsync;


    RenderCallback(HttpServletRequest req, HttpServletResponse resp) {
      this.req = req;
      this.resp = resp;
      this.useAsync = false;
    }


    public void startAsync() {
      if (! req.isAsyncSupported()) {
        throw new XBosonException.NotImplements("Servlet Async mode");
      }
      this.ac = req.startAsync();
      this.req = (HttpServletRequest) ac.getRequest();
      this.resp = (HttpServletResponse) ac.getResponse();
      useAsync = true;
    }


    @Override
    public void render(byte[] content, String mime, String fullpath) {
      try {
        resp.setContentType(mime);
        OutputStream out = resp.getOutputStream();
        out.write(content);
        out.flush();
      } catch(Exception err) {
        log.error("Render fail", err.getMessage());
      } finally {
        if (useAsync) ac.complete();
      }
    }


    @Override
    public void error(String message) {
      try {
        log.error("Render Error", message);
        resp.sendError(500, message);
      } catch (IOException e) {
        log.error("Render Error Callback", e.getMessage());
      } finally {
        if (useAsync) ac.complete();
      }
    }
  }
}
