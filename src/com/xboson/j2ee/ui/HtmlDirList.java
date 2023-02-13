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
// 文件创建日期: 17-11-20 下午1:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/html/HtmlDirList.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.fs.redis.RedisFileAttr;
import com.xboson.util.Tool;

import javax.activation.FileTypeMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Set;


public class HtmlDirList {

  static com.xboson.script.lib.Path tool = new com.xboson.script.lib.Path();


  /**
   * 生成目录列表
   *
   * @param html 输出
   * @param files
   * @param baseurl 网络路径
   * @throws IOException
   */
  public static void toHtml(Writer html, Set<RedisFileAttr> files, String baseurl)
          throws IOException {

    html.write("<html><head>");
    style(html);
    html.write("</head><body><h1>Directory ");
    html.write(baseurl);
    html.write("</h1><table>");

    html.write("<tr><td><a href='");
    html.write(tool.normalize(baseurl + "/../"));
    html.write("'>[..]</a></td><tr>");

    FileTypeMap types = MimeTypeFactory.getFileTypeMap();

    for (RedisFileAttr p : files) {
      html.write("<tr>");

      String name = p.path;
      html.write("<td><a href='");
      html.write(tool.normalize(baseurl + "/" + name));
      html.write("'>");
      html.write(name);
      html.write("</a></td>");

      html.write("<td>");
      html.write(p.isDir() ? "[DIR]" : types.getContentType(name));
      html.write("</td>");

      html.write("<td>");
      html.write(p.lastModify > 0 ? Tool.formatDate(new Date(p.lastModify)) : "");
      html.write("</td>");

      html.write("</tr>");
    }

    html.write("</table><section class='right'>");
    html.write(new Date().toString());
    html.write("<hr/> J.yanming");
    html.write("</section></html>");
    html.flush();
  }


  public static void style(Writer html) throws IOException {
    html.write("<style>");
    html.write("body { padding: 50px } ");
    html.write("table {} ");
    html.write("td { border-bottom:1px solid #c3c3c3; padding: 3px 30px; } ");
    html.write("section { margin: 50px 0 } ");
    html.write(".right { text-align: right; color: #1e1140 } ");
    html.write("hr { border: 1px dashed  #9e971d; border-top:0; margin: 1px;} ");
    html.write("</style>");
  }

}
