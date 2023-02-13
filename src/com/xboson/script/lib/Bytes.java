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
// 文件创建日期: 18-8-18 上午9:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/Bytes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.been.IJson;
import com.xboson.been.JsonHelper;
import com.xboson.been.XBosonException;
import com.xboson.script.IVisitByScript;
import com.xboson.util.Hex;
import com.xboson.util.c0nst.IConstant;

import java.util.Arrays;


/**
 * 字节数组对象, 可以将字节数组和字符串互相转换.
 * 该对象设计为不可变类.
 */
public class Bytes implements IJson, IVisitByScript {

  private byte[] key;
  private String s_key;


  /**
   * @param base64url - base64url 编码字符串
   */
  public Bytes(String base64url) {
    this.s_key = base64url;
  }


  public Bytes(byte[] k) {
    this.key = k;
  }


  /**
   * 0 字节数组
   */
  public Bytes() {
    this.key = new byte[0];
  }


  @Override
  public String toString() {
    if (s_key == null) {
      s_key = Hex.encode64(key);
    }
    return s_key;
  }


  public String toString(String coding) {
    return Hex.encode(coding, bin());
  }


  public String toHex() {
    if (key == null) {
      if (s_key == null) {
        return null;
      }
      bin();
    }
    return Hex.upperHex(key);
  }


  public byte[] bin() {
    if (key == null) {
      key = Hex.decode64(s_key);
    }
    return key;
  }


  public Bytes sub(int begin, int end) {
    byte[] src = bin();
    if (end <= 0) {
      end = src.length;
    }
    if (begin < 0) {
      throw new XBosonException.BadParameter("begin", "Cannot be less than zero");
    }
    if (begin >= end) {
      throw new XBosonException.BadParameter("begin", "Cannot be greater than end");
    }
    return new Bytes(Arrays.copyOfRange(src, begin, end));
  }


  public Bytes sub(int begin) {
    return sub(begin, -1);
  }


  public String toJavaString() {
    return new String(bin(), IConstant.CHARSET);
  }


  @Override
  public String toJSON() {
    return JsonHelper.toJSON(toString());
  }


  public Bytes concat(Bytes other) {
    byte[] a = this.bin();
    byte[] b = other.bin();
    byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return new Bytes(c);
  }


  public int length() {
    return bin().length;
  }

}
