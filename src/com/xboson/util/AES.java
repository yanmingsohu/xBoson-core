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
// 文件创建日期: 2017年11月1日 上午11:11:48
// 原始文件路径: xBoson/src/com/xboson/util/AES.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * session 使用该算法加密
 */
public class AES implements IConstant {

  private static final int keylen = 16 * 8;
  private static final int itercount = 5999;
  private static final IvParameterSpec iv;
  private static final byte[] salt;

  static {
    salt = "xBoson XX 2017.11.2 --jym".getBytes();

    // 随机生成且不可变化, 用于加强密钥
    iv = new IvParameterSpec(new byte[]{
      0x33, 0x16, 0x71, 0x11,
      0x67, (byte)0x81, 0x01, 0x41,
      (byte)0x91, 0x38, 0x11, 0x33,
      0x63, 0x44, 0x21, 0x41,
    });
  }


  /**
   * 生成 aes 密钥
   * @throws Exception
   */
  public static byte[] aesKey(String pass) {
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance(PBK1_NAME);
      PBEKeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, itercount, keylen);
      SecretKey secretKey = skf.generateSecret(spec);
      return secretKey.getEncoded();
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  public static byte[] Encode(byte[] data, byte[] password) {
    try {
      Cipher c = Cipher.getInstance(AES_C_P_NAME);
      SecretKeySpec key = new SecretKeySpec(password, AES_NAME);
      c.init(ENCRYPT_MODE, key, iv);
      return c.doFinal(data);
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  public static byte[] Decode(byte[] data, byte[] password) {
    try {
      Cipher c = Cipher.getInstance(AES_C_P_NAME);
      SecretKeySpec key = new SecretKeySpec(password, AES_NAME);
      c.init(DECRYPT_MODE, key, iv);
      return c.doFinal(data);
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }
}
