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
// 文件创建日期: 19-1-17 下午9:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/LimitInputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;


/**
 * 流量限制的输入流, 当读取流量达到上限, 继续读取会抛出异常.
 */
public class LimitInputStream extends InputStream {

  private InputStream inputStream;
  private long bytesRead;
  private long limit;


  /**
   * 便捷方法, 返回 target 流的限流器,
   * 如果 limit <=0 表示没有限制, 此时返回 target 本身
   *
   * @param target 被包装输入流
   * @param limit 读取字节上限
   * @return 返回限流器或在没有限制时返回 target.
   */
  public static InputStream wrap(InputStream target, long limit) {
    return limit > 0
            ? new LimitInputStream(target, limit)
            : target;
  }


  /**
   * 包装一个输入流, 并设定读取上限值
   * @param wrap 被包装的输入流
   * @param limit 读取字节上限, 必须大于 0
   */
  public LimitInputStream(InputStream wrap, long limit) {
    if (limit <= 0) throw new
            XBosonException.BadParameter("limit", "Less than 0");
    if (wrap == null) throw new
            XBosonException.NullParamException("wrap");

    this.inputStream = wrap;
    this.bytesRead = 0;
    this.limit = limit;
  }


  public int read() throws IOException {
    addCheck(1);
    return inputStream.read();
  }


  public int read(@NotNull byte[] bytes) throws IOException {
    int r = inputStream.read(bytes);
    addCheck(r);
    return r;
  }


  public int read(@NotNull byte[] bytes, int i, int i1) throws IOException {
    int r = inputStream.read(bytes, i, i1);
    addCheck(r);
    return r;
  }


  public long skip(long l) throws IOException {
    addCheck(l);
    return inputStream.skip(l);
  }


  public int available() throws IOException {
    return inputStream.available();
  }


  public void close() throws IOException {
    inputStream.close();
  }


  public void mark(int i) {
    inputStream.mark(i);
  }


  public void reset() throws IOException {
    inputStream.reset();
  }


  public boolean markSupported() {
    return inputStream.markSupported();
  }


  private void addCheck(long r) {
    if (r > 0) {
      bytesRead += r;
      if (bytesRead > limit) {
        throw new XBosonException(
                "Http Body is greater than "+ limit +" bytes.");
      }
    }
  }
}
