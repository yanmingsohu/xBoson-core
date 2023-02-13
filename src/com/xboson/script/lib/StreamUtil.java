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
// 文件创建日期: 18-6-5 下午2:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/StreamUtil.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.c0nst.IConstant;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class StreamUtil {

  private Base64.Encoder bec;
  private Base64.Decoder bdc;


  public StreamUtil() {
    bec = Base64.getEncoder();
    bdc = Base64.getDecoder();
  }

  public JsOutputStream openGzipOutputStream(JsOutputStream out) throws IOException {
    GZIPOutputStream gzip = new GZIPOutputStream(out, true);
    return new JsOutputStream(gzip) {
      public void finish() throws IOException {
        gzip.finish();
      }
    };
  }


  public JsInputStream openGzipInputStream(JsInputStream in) throws IOException {
    GZIPInputStream gzip = new GZIPInputStream(in);
    return new JsInputStream(gzip);
  }


  public JsOutputStream openBase64OutputStream(JsOutputStream out) {
    return new JsOutputStream(bec.wrap(out));
  }


  public JsInputStream openBase64InputStream(JsInputStream in) {
    return new JsInputStream(bdc.wrap(in));
  }


  public JsOutputStream openStringBufferOutputStream() {
    StringBufferOutputStream buf = new StringBufferOutputStream();
    return new JsOutputStream(buf);
  }


  @SuppressWarnings("deprecation")
  public JsInputStream openStringInputStream(String s) {
    StringBufferInputStream r = new StringBufferInputStream(s);
    return new JsInputStream(r);
  }


  public LineNumberReader openLineReader(JsInputStream in) {
    return new LineNumberReader(new InputStreamReader(in, IConstant.CHARSET));
  }


  public JsOutputStream openXMLOutputStream(JsOutputStream out) throws XMLStreamException {
    return new JsOutputStream(new XmlContentWriter(out));
  }


  /**
   * 在写出 xml 文本时, 对特殊字符做转义.
   */
  public static class XmlContentWriter extends OutputStream {
    private JsOutputStream ori;

    public XmlContentWriter(JsOutputStream ori) {
      this.ori = ori;
    }

    @Override
    public void write(int i) throws IOException {
      if (i >= 0x80) {
        ori.write(i);
      } else {
        switch (i) {
          case '<' : ori.write("&lt;"); break;
          case '>' : ori.write("&gt;"); break;
          case '&' : ori.write("&amp;"); break;
          case '"' : ori.write("&quot;"); break;
          case '\'': ori.write("&apos;"); break;
          default  : ori.write(i);
        }
      }
    }
  }
}
