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
import java.io.UnsupportedEncodingException;
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
    setFileName(response, file);

    OutputStream out = response.getOutputStream();
    InputStream _in = (InputStream) ret_root.get(MAP_KEY_STREAM);
    Streams.copy(_in, out, true);
  }


  public static void setFileName(HttpServletResponse resp, String filename)
          throws UnsupportedEncodingException {
    filename = URLEncoder.encode(filename, "UTF-8");
    filename = filename.replaceAll("\\+", "%20");

    resp.setHeader("content-type", MIME_BIN);
    resp.setHeader("Content-Disposition",
            "attachment; filename=\""+ filename +"\"");
  }
}
