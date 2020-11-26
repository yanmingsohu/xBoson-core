////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-6-5 下午2:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/JsInputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

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
    StringBuilder buf = new StringBuilder();
    byte[] bs = new byte[128];
    int rlen = read(bs, 0, bs.length);
    if (rlen <= 0) return null;

    do {
      buf.append(new String(bs, 0, rlen, charsetName));
      rlen = read(bs, 0, bs.length);
    } while (rlen > 0);
    return buf.toString();
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
