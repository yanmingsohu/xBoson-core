////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
      buf = Arrays.copyOf(buf, buf.length * 2);
    }
  }


  public String toString() {
    return new String(buf, 0, pos);
  }


  public ByteBuffer toBuffer() {
    return ByteBuffer.wrap(buf, 0, pos);
  }


  public byte[] toBytes() {
    return Arrays.copyOf(buf, pos);
  }


  /**
   * 返回一个 Write, 包装了自身, 目的是最终可以获取原始字节.
   * 在写入完成后, 调用 Writer.flush 后才能保证缓冲区同步最新的数据.
   */
  public Writer openWrite() {
    return new OutputStreamWriter(this);
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
}
