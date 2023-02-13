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
// 文件创建日期: 17-11-23 下午1:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/impl/LogServletOutputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.emu;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.StringBufferOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


/**
 * 控制台必须是 utf8 编码, 否则乱码
 */
public class LogServletOutputStream extends ServletOutputStream {

  private StringBufferOutputStream out;
  private Log log;


  public LogServletOutputStream(Log log) {
    this.log = log;
    out = new StringBufferOutputStream(1000);
  }


  @Override
  public boolean isReady() {
    return true;
  }


  @Override
  public void setWriteListener(WriteListener writeListener) {
  }


  @Override
  public void write(int i) throws IOException {
    out.write(i);
  }


  @Override
  public void flush() throws IOException {
    log.info(out.toString());
    out.clear();
  }


  @Override
  public void close() throws IOException {
    flush();
    out = null;
    log = null;
  }


  public Writer openWriter() {
    return out.openWrite();
  }
}
