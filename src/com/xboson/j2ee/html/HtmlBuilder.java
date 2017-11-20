////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-20 下午1:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/html/HtmlBuilder.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.html;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;


public class HtmlBuilder {

  static com.xboson.script.lib.Path tool = new com.xboson.script.lib.Path();


  /**
   * 生成目录列表
   *
   * @param html 输出
   * @param local 本地路径
   * @param baseurl 网络路径
   * @throws IOException
   */
  public static void listDir(Writer html, Path local, String baseurl) throws IOException {
    DirectoryStream<Path> dirs = Files.newDirectoryStream(local);

    html.write("<html><head>");
    style(html);
    html.write("</head><body><h1>Directory ");
    html.write(baseurl);
    html.write("</h1><table>");

    html.write("<tr><td><a href='");
    html.write(tool.normalize(baseurl + "/../"));
    html.write("'>[..]</a></td><tr>");

    for (Path p : dirs) {
      html.write("<tr>");

      String name = p.getFileName().toString();
      html.write("<td><a href='");
      html.write(tool.normalize(baseurl + "/" + name));
      html.write("'>");
      html.write(name);
      html.write("</a></td>");

      html.write("<td>");
      html.write(Files.isDirectory(p) ? "[DIR]" : "file");
      html.write("</td>");

      html.write("<td>");
      html.write(Files.getLastModifiedTime(p)+"");
      html.write("</td>");

      html.write("</tr>");
    }

    html.write("</table><section class='right'>");
    html.write(new Date().toString());
    html.write("</section></html>");
    html.flush();
  }


  public static void style(Writer html) throws IOException {
    html.write("<style>");
    html.write("body { padding: 50px } ");
    html.write("table {} ");
    html.write("td { border-bottom:1px solid #888; padding: 3px 30px; } ");
    html.write("section { padding: 50px 0 } ");
    html.write(".right { text-align: right; } ");
    html.write("</style>");
  }

}
