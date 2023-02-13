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
// 文件创建日期: 2017年11月4日 上午9:29:03
// 原始文件路径: xBoson/src/com/xboson/util/StringBufferOutputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 字符串缓冲区输入流, 用流的方式写入字符串, 尽可能避免数据的复制.
 * toString()/toBuffer() 获取结果
 */
public class StringBufferOutputStream extends OutputStream {

  public static final int DEFAULT_SIZE = 1024;

  private byte[] buf;
  private int pos = 0;


  public StringBufferOutputStream() {
    this(DEFAULT_SIZE);
  }


  public StringBufferOutputStream(int init_size) {
    this.buf = new byte[init_size];
  }


  /**
   * 将输入流中的所有数据写入缓冲区, 该函数返回后, src 被关闭.
   * @param src
   * @throws IOException
   */
  public void write(InputStream src) throws IOException {
    write(src, true);
  }


  public void write(InputStream src, boolean closeInput) throws IOException {
    if (src == null) {
      throw new XBosonException.NullParamException("InputStream src");
    }
    Tool.copy(src, this, closeInput);
  }


  @Override
  public void write(int b) throws IOException {
    buf[pos] = (byte) b;
    if (++pos >= buf.length) {
      buf = Arrays.copyOf(buf, buf.length << 1);
    }
  }


  /**
   * 弹出缓冲区中的最后一个字符, 这件使缓冲区长度减一.
   * 在空缓冲区上继续 pop 会抛出异常.
   */
  public byte pop() {
    return buf[--pos];
  }


  /**
   * 如果最终读取的是字符/字符串, 则 toString() 比 toBuffer()/toBytes() 更高效.
   * @return 缓冲区转换为字符串
   */
  public String toString() {
    return new String(buf, 0, pos, IConstant.CHARSET);
  }


  public String toString(String charsetName) throws UnsupportedEncodingException {
    return new String(buf, 0, pos, charsetName);
  }


  public ByteBuffer toBuffer() {
    return ByteBuffer.wrap(toBytes());
  }


  public byte[] toBytes() {
    return Arrays.copyOf(buf, pos);
  }


  /**
   * 返回一个 Write, 包装了自身, 目的是最终可以获取原始字节.
   * 在写入完成后, 调用 Writer.flush 后才能保证缓冲区同步最新的数据.
   */
  public Writer openWrite() {
    return new OutputStreamWriter(this, IConstant.CHARSET);
  }


  /**
   * @see #openWrite()
   * @param charsetName 使用指定的编码创建 Writer
   */
  public Writer openWrite(String charsetName) {
    try {
      return new OutputStreamWriter(this, charsetName);
    } catch (UnsupportedEncodingException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 创建一个读取器用于读取已经写入自身缓冲区的数据.
   * 在返回该 InputStream 后继续写入数据, 已经返回的流不会同步更新.
   * 可以多次调用, 返回多个 InputStream.
   * @return
   */
  public InputStream openInputStream() {
    return new ByteArrayInputStream(buf, 0, pos);
  }


  /**
   * 重置缓冲区
   */
  public void clear() {
    pos = 0;
    Arrays.fill(buf, (byte) 0);
  }
}
