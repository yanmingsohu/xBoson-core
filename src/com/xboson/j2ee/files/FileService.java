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
// 文件创建日期: 17-11-26 上午11:42
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/files/FileService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.been.XBosonException;
import com.xboson.event.timer.EarlyMorning;
import com.xboson.j2ee.container.XResponse;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Checker;
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
import java.util.*;


/**
 * 文件上传下载服务
 *
 * @see com.xboson.init.Startup 配置到容器
 */
@MultipartConfig(maxFileSize=100 * 1024 * 1024)
public class FileService extends HttpServlet {

  public final static String UPLOAD_HTML = "/WEB-INF/tool-page/upload.html";
  private Log log;


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log = LogFactory.create();

    if (SysConfig.me().readConfig().enableUploadClear) {
      TimerTask task = PrimitiveOperation.me().createCleanTask();
      EarlyMorning.add(task);
    }
  }


  /**
   * 上传文件
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    XResponse ret = XResponse.get(req);
    String dirname = DirectoryGenerate.get(req);
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
        int count = PrimitiveOperation.me().updateFile(
                dirname, filename, type, p.getInputStream());

        if (count == 1) {
          ++new_count;
        } else {
          ++update_count;
        }

        info.add(new FileInfo(dirname, filename));
        log.debug("Saved file", dirname, filename);
      }
    }

    ret.bindResponse("updated_files", update_count);
    ret.bindResponse("created_files", new_count);
    ret.response();
  }


  /**
   * 下载文件
   * Http 参数:
   *  file_name - 文件名
   *  dir_name - 路径
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
    String dir_name  = req.getParameter("dir_name");

    if (Tool.isNulStr(file_name)) {
      ret.responseMsg("文件名不能为空 [file_name]", 1);
      return;
    }

    String base = DirectoryGenerate.get(req);
    if (dir_name != null) {
      Checker.me.safepath(dir_name, "dir_name");
      dir_name = Tool.normalize(base +'/'+ dir_name);
    } else {
      dir_name = base;
    }

    try (FileInfo info = PrimitiveOperation.me().openReadFile(dir_name, file_name)) {
      resp.setDateHeader("Last-Modified", info.last_modified);
      resp.setContentType(info.type);
      resp.setHeader("Content-Disposition",
              "attachment; filename=" + file_name);
      Tool.copy(info.input, resp.getOutputStream(), true);

      log.debug("Dir", dir_name, ", File", file_name);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }

}
