////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-12-30 上午9:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/resp/StreamResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.xboson.j2ee.container.IXResponse;
import org.apache.commons.fileupload.util.Streams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;


public class StreamResponse implements IXResponse {

  private static final String MIME_BIN = "application/octet-stream";

  /**
   * 放入应答数据集时使用这个键名
   */
  public static final String MAP_KEY_STREAM = "__STREAM_READER";
  public static final String MAP_KEY_FILENAME = "__STREAM_FILENAME";


  @Override
  public void response(HttpServletRequest request, HttpServletResponse response,
                       Map<String, Object> ret_root) throws IOException {
    String file = (String) ret_root.get(MAP_KEY_FILENAME);
    file = URLEncoder.encode(file, "UTF-8");

    response.setHeader("content-type", MIME_BIN);
    response.setHeader("Content-Disposition",
            "attachment; filename=\""+ file +"\"");

    OutputStream out = response.getOutputStream();
    InputStream _in = (InputStream) ret_root.get(MAP_KEY_STREAM);
    Streams.copy(_in, out, true);
  }
}
