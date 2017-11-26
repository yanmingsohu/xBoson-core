////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-26 上午11:42
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/files/FileService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.been.FileInfo;
import com.xboson.been.SessionData;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.j2ee.container.XResponse;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * 文件上传下载服务
 *
 * @see com.xboson.init.Startup 配置到容器
 */
@MultipartConfig(maxFileSize=100 * 1024 * 1024)
public class FileService extends HttpServlet {

  public final static String UPLOAD_HTML = "/WEB-INF/tool-page/upload.html";
  private Log log;
  private ConnectConfig db;


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log = LogFactory.create();
    db = SysConfig.me().readConfig().db;
  }


  /**
   * 上传文件
   *
   * @param req
   * @param resp
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    XResponse ret = XResponse.get(req);
    String dirname = getDirectory(req);
    int new_count = 0;
    int update_count = 0;

    Collection<Part> all = req.getParts();
    if (all.isEmpty()) {
      ret.setMessage("Must post some file with `multipart/form-data`");
      return;
    }

    Iterator<Part> it = all.iterator();
    List<FileInfo> info = new ArrayList<>(all.size());
    ret.setData(info);

    while (it.hasNext()) {
      Part p = it.next();
      String filename = p.getSubmittedFileName();
      String type = p.getContentType();

      if (Tool.isNulStr(filename) != true && type != null) {
        String id = Tool.uuid.zip();

        try (SqlResult sr = SqlReader.query("file_create_or_replace.sql",
                db, id, filename, dirname, type, p.getInputStream())) {
          if (sr.getUpdateCount() == 1) {
            ++new_count;
          } else {
            ++update_count;
          }
          info.add(new FileInfo(dirname, filename));
          log.debug("Saved file", dirname, filename);
        }
      }
    }

    ret.bindResponse("updated_files", update_count);
    ret.bindResponse("created_files", new_count);
    ret.response();
  }


  /**
   * 下载文件
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {

    if (req.getParameter("page") != null) {
      InputStream in = req.getServletContext().getResourceAsStream(UPLOAD_HTML);
      Tool.copy(in, resp.getOutputStream(), true);
      return;
    }

    XResponse ret = XResponse.get(req);
    String file_name = req.getParameter("file_name");

    if (Tool.isNulStr(file_name)) {
      ret.responseMsg("文件名不能为空 [file_name]", 1);
      return;
    }

    String dir_name = getDirectory(req);
    log.debug("Dir", dir_name, ", File", file_name);

    try (SqlResult sr = SqlReader.query(
            "file_open.sql", db, dir_name, file_name)) {
      ResultSet rs = sr.getResult();

      if (rs.next()) {
        resp.setContentType( rs.getString("content-type") );

        resp.setDateHeader("Last-Modified",
                rs.getTimestamp("update-time").getTime());

        Tool.copy(rs.getBinaryStream("content"),
                  resp.getOutputStream(),
                  true);
      } else {
        ret.responseMsg("Not found file: " + file_name, 404);
      }
    } catch (SQLException e) {
      throw new XBosonException.XSqlException(e);
    }
  }


  /**
   * 路径生成规则:
   *    当用户登录一级目录为用户 id, 否则为 temporary;
   *    二级目录为 servlet 服务路径之后的路径字符串, 使用 '/' 拼接
   */
  private String getDirectory(HttpServletRequest req) throws ServletException {
    SessionData sess = SessionData.get(req);
    UrlSplit sp = new UrlSplit(req);
    String dirname;

    if (sess == null || sess.login_user == null) {
      dirname = "/temporary";
    } else {
      dirname = "/" + sess.login_user.userid;
    }

    if (sp.getLast() != null) {
      dirname += sp.getLast();
    }

    return dirname;
  }

}
