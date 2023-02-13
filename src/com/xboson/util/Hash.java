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
// 文件创建日期: 18-7-13 下午5:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/Hash.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.chain.SignNode;
import com.xboson.util.c0nst.IConstant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


/**
 * 摘要封装, 支持除了 byte[] 以外的摘要计算
 */
public class Hash {

  public static final String DEFAULT_ALGORITHM = "SHA-256";

  private final MessageDigest md;
  private byte[] digest;
  private long byteCount = 0;


  /**
   * 默认使用 SHA-256 计算摘要
   */
  public Hash() {
    this(DEFAULT_ALGORITHM);
  }


  public Hash(String algorithm) {
    try {
      md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new XBosonException(e);
    }
  }


  public Hash(String algorithm, String provider) {
    try {
      md = MessageDigest.getInstance(algorithm, provider);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 返回经过签名数据的字节数
   */
  public long updatedBytes() {
    return byteCount;
  }


  public void update(String s) {
    byte[] b = s.getBytes(IConstant.CHARSET);
    md.update(b);
    byteCount += b.length;
  }


  public void update(byte[] b) {
    md.update(b);
    byteCount += b.length;
  }


  public void update(byte[] b, int begin, int len) {
    md.update(b, begin, len);
    byteCount += len;
  }


  public void update(Date d) {
    update(d.getTime());
  }


  public void update(long l) {
    md.update((byte) (l & 0xFF));
    md.update((byte) ((l>>8 ) & 0xFF));
    md.update((byte) ((l>>16) & 0xFF));
    md.update((byte) ((l>>24) & 0xFF));

    md.update((byte) ((l>>32) & 0xFF));
    md.update((byte) ((l>>40) & 0xFF));
    md.update((byte) ((l>>48) & 0xFF));
    md.update((byte) ((l>>56) & 0xFF));
    byteCount += 8;
  }


  public void update(int l) {
    md.update((byte) (l & 0xFF));
    md.update((byte) ((l>>8 ) & 0xFF));
    md.update((byte) ((l>>16) & 0xFF));
    md.update((byte) ((l>>24) & 0xFF));
    byteCount += 4;
  }


  /**
   * sn 不能为空
   */
  public void update(SignNode sn) {
    SignNode n = sn;
    do {
      update(n.id);
      update(n.sign);
      n = n.next;
    } while (n != null);
  }


  public byte[] digest() {
    if (digest == null) {
      digest = md.digest();
    }
    return digest;
  }


  /**
   * 返回摘要的 16 进制字符串.
   */
  public String digestStr() {
    return Hex.lowerHex(md.digest());
  }


  public static byte[] sha256(byte[] i) {
    Hash h = new Hash();
    h.update(i);
    return h.digest();
  }


  public static byte[] join(byte[] a, byte[] b) {
    return Hex.join(a, b);
  }
}
