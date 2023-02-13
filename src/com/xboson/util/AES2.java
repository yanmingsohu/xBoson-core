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
// 文件创建日期: 17-11-23 上午8:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/AES2.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;


/**
 * api 使用该算法加密, 存储该对象的实例使用效率最佳
 */
public class AES2 implements IConstant {

  private SecretKeySpec key;


  /**
   * 创建 aes 加密实例, 该对象可以缓存, 并且多线程安全
   * @param keystr 密钥
   */
  public AES2(String keystr) {
    init(keystr.getBytes(CHARSET));
  }


  /**
   * 创建 aes 加密实例, 该对象可以缓存, 并且多线程安全
   * @param keybin 密钥
   */
  public AES2(byte[] keybin) {
    init(keybin);
  }


  private void init(byte[] keybin) {
    key = genKey(keybin);
  }


  /**
   * 生成密钥
   */
  public static SecretKeySpec genKey(byte[] keybin) {
    try {
      KeyGenerator kgen = KeyGenerator.getInstance(AES_NAME);
      SecureRandom secureRandom = SecureRandom.getInstance(SHA1_PRNG_NAME);
      secureRandom.setSeed(keybin);
      kgen.init(128, secureRandom);

      SecretKey secretKey = kgen.generateKey();
      byte[] enCodeFormat = secretKey.getEncoded();
      return new SecretKeySpec(enCodeFormat, AES_NAME);
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 返回加密后数据的 HEX 字符串形式
   * <b>encrypt 与 decrypt 不对称, 这是为了另一个使用该算法的类而做的优化</b>
   */
  public String encrypt(String code) {
    return Hex.upperHex(encryptBin(code.getBytes(CHARSET)));
  }


  /**
   * 返回解密后的二进制数据, mi 为 HEX 形式的加密数据
   */
  public byte[] decrypt(String mi) {
    return decryptBin(Hex.parse(mi));
  }


  /**
   * 加密二进制数据, 返回加密原始数据
   */
  public byte[] encryptBin(byte[] srcBytes) {
    try {
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(ENCRYPT_MODE, key);
      return cipher.doFinal(srcBytes);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 解密二进制数据, 返回原始数据
   */
  public byte[] decryptBin(byte[] secret) {
    try {
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(DECRYPT_MODE, key);
      return cipher.doFinal(secret);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }
}
