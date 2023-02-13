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
// 文件创建日期: 18-6-5 下午2:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/JsOutputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.util.c0nst.IConstant;

import java.io.IOException;
import java.io.OutputStream;


/**
 * 将 java 流对象包装后暴露给 js 对象
 */
public class JsOutputStream extends OutputStream {

  private OutputStream ori;


  public JsOutputStream(OutputStream o) {
    this.ori = o;
  }


  /**
   * 返回原始输出流
   */
  protected OutputStream original() {
    return ori;
  }


  @Override
  public void write(byte[] bytes) throws IOException {
    ori.write(bytes);
  }


  @Override
  public void write(byte[] bytes, int i, int i1) throws IOException {
    ori.write(bytes, i, i1);
  }


  public void write(Bytes bs) throws IOException {
    ori.write(bs.bin());
  }


  @Override
  public void flush() throws IOException {
    ori.flush();
  }


  @Override
  public void close() throws IOException {
    ori.close();
  }


  @Override
  public void write(int i) throws IOException {
    ori.write(i);
  }


  /**
   * 有一些流不希望关闭时关闭底层流, 但是又需要完成最后的输出, 则实现该方法.
   * 默认什么都不做.
   */
  public void finish() throws IOException {
  }


  /**
   * 读取 from 中的数据写入输出流
   * @param from 数据来源
   * @param begin from 开始的字节
   * @param len 从 from 读取的字节长度
   */
  public void write(Buffer.JsBuffer from, int begin, int len) throws IOException {
    for (int i=0; i<len; ++i) {
      int b = from.readInt8(i + begin) & 0xFF;
      write(b);
    }
  }


  public void write(String str) throws IOException {
    write(str.getBytes(IConstant.CHARSET));
  }


  @Override
  public String toString() {
    return ori.toString();
  }
}
