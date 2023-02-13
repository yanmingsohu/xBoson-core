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
// 文件创建日期: 17-11-12 下午5:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/Uuid.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.script.JSObject;
import com.xboson.util.Hex;

import java.nio.ByteBuffer;
import java.util.UUID;


public class Uuid extends JSObject {

  public static final long HALF = (long)(Long.MAX_VALUE / 2);
  public static final long v1 = 1l << 12;
  public static final long versionMask = ~(0xF << 12);

  private static long id = (long)(Math.random() * HALF);


  /**
   * 返回标准 UUID 字符串, 基于时间生成可能重复.
   */
  public String v1() {
    return v1obj().toString();
  }


  /**
   * 返回标准 UUID 字符串, 可以生成健壮的随机 UUID.
   */
  public String v4() {
    return v4obj().toString();
  }


  /**
   * 返回标准 UUID 对象, 基于时间生成可能重复.
   */
  public UUID v1obj() {
    long m = System.currentTimeMillis();
    long l = ++id;
    if (l < HALF) l = -l;
    if (m < HALF) m = -m;
    m = m & versionMask | v1;
    return new UUID(m, l);
  }


  /**
   * 返回标准 UUID 对象, 可以生成健壮的随机 UUID.
   */
  public UUID v4obj() {
    return UUID.randomUUID();
  }


  /**
   * 生成原先 DS 平台的 UUID 字符串
   */
  public String ds() {
    return ds(v4obj());
  }


  /**
   * 生成原先 DS 平台的 UUID 字符串
   */
  public String ds(UUID id) {
    byte[] bytes = toBytes(id);
    return Hex.lowerHex(bytes);
  }


  /**
   * 解析原 DS 平台字符串到 UUID 对象
   */
  public UUID parseDS(String ds) {
    return UUID.fromString(
            ds.substring( 0,  8) + "-" +
            ds.substring( 8, 12) + "-" +
            ds.substring(12, 16) + "-" +
            ds.substring(16, 20) + "-" +
            ds.substring(20, 32)
    );
  }


  /**
   * 转换为 16 字节
   */
  public byte[] toBytes(UUID id) {
    ByteBuffer buf = ByteBuffer.allocate(16);
    buf.putLong(0, id.getMostSignificantBits());
    buf.putLong(8, id.getLeastSignificantBits());
    return buf.array();
  }


  public Bytes getBytes(UUID id) {
    return new Bytes(toBytes(id));
  }


  /**
   * 生成压缩的 UUID 字符串
   */
  public String zip(UUID id) {
    byte[] b = toBytes(id);
    return Hex.encode64(b);
  }


  /**
   * 解压缩使用 zip() 压缩的字符串, 还原为 UUID 对象
   */
  public UUID unzip(String z) {
    byte[] b = Hex.decode64(z);
    ByteBuffer buf = ByteBuffer.wrap(b);
    return new UUID(buf.getLong(0), buf.getLong(8));
  }


  /**
   * 生成压缩的 UUID 字符串, 长度 24 字符
   */
  public String zip() {
    return zip(v4obj());
  }
}
