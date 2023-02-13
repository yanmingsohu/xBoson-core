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
// 文件创建日期: 18-6-5 下午2:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/JsInputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.c0nst.IConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 将 java 流对象包装后暴露给 js 对象
 */
public class JsInputStream extends InputStream {

  private final static int BUFSIZE = 256;
  private InputStream ori;


  public JsInputStream(InputStream ori) {
    this.ori = ori;
  }


  /**
   * 将读入的数据写到 out 流中, 返回输出的字节
   */
  public int pipe(OutputStream out) throws IOException {
    byte[] buf = new byte[BUFSIZE];
    int total = 0;
    for (;;) {
      int len = ori.read(buf);
      if (len <= 0) {
        break;
      }
      out.write(buf, 0, len);
      total += len;
    }
    return total;
  }


  /**
   * 返回原始输出流
   */
  protected InputStream original() {
    return ori;
  }


  @Override
  public int read() throws IOException {
    return ori.read();
  }


  @Override
  public int read(byte[] bytes) throws IOException {
    return ori.read(bytes);
  }


  @Override
  public int read(byte[] bytes, int i, int i1) throws IOException {
    return ori.read(bytes, i, i1);
  }


  /**
   * 读取指定的字节数据到 buf 中
   * @param tar 存储目标
   * @param begin buf 的开始位置
   * @param len 读取 len 个字节并写入
   * @return 写入/读取的字节
   */
  public int read(Buffer.JsBuffer tar, int begin, int len) throws IOException {
    for (int i=0; i<len; ++i) {
      int r = ori.read();
      if (r <= 0) {
        return i;
      }
      tar.writeUInt8(r, begin+i);
    }
    return len;
  }


  public int read(Buffer.JsBuffer tar, int begin) throws IOException {
    return read(tar, begin, tar.getLength());
  }


  public int read(Buffer.JsBuffer tar) throws IOException {
    return read(tar, 0, tar.getLength());
  }


  public Bytes readBytes(int len) throws IOException {
    byte[] bs = new byte[len];
    int rlen = read(bs, 0, len);
    if (rlen < 0) return null;
    if (rlen != len) {
      byte[] x = new byte[rlen];
      System.arraycopy(bs, 0, x, 0, rlen);
      bs = x;
    }
    return new Bytes(bs);
  }


  public Bytes readBytes() throws IOException {
    return readBytes(ori.available());
  }


  public String readString(String charsetName) throws IOException {
    StringBufferOutputStream buf = new StringBufferOutputStream(ori.available());
    buf.write(this, false);
    String ret = buf.toString(charsetName);
    if (ret.length() <= 0) {
      return null;
    }
    return ret;
  }


  public String readString() throws IOException {
    return readString(IConstant.CHARSET_NAME);
  }


  @Override
  public long skip(long l) throws IOException {
    return ori.skip(l);
  }


  @Override
  public int available() throws IOException {
    return ori.available();
  }


  @Override
  public void close() throws IOException {
    ori.close();
  }


  @Override
  public void mark(int i) {
    ori.mark(i);
  }


  @Override
  public void reset() throws IOException {
    ori.reset();
  }


  @Override
  public boolean markSupported() {
    return ori.markSupported();
  }
}
