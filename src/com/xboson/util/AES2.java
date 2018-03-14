////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
 * api 使用该算法加密, 该算法可实例化
 */
public class AES2 implements IConstant {

  private SecretKeySpec key;


  /**
   * 创建 aes 加密实例, 该对象可以缓存, 并且多线程安全
   * @param keystr 密钥
   */
  public AES2(String keystr) {
    try {
      KeyGenerator kgen = KeyGenerator.getInstance(AES_NAME);
      SecureRandom secureRandom = SecureRandom.getInstance(SHA1_PRNG_NAME);
      secureRandom.setSeed(keystr.getBytes(CHARSET));
      kgen.init(128, secureRandom);

      SecretKey secretKey = kgen.generateKey();
      byte[] enCodeFormat = secretKey.getEncoded();
      key = new SecretKeySpec(enCodeFormat, AES_NAME);
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  public String encrypt(String code) {
    try {
      byte[] srcBytes = code.getBytes(CHARSET);
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(ENCRYPT_MODE, key);
      return Hex.upperHex(cipher.doFinal(srcBytes));
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public byte[] decrypt(String mi) {
    try {
      byte[] srcBytes = Hex.parse(mi);
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(DECRYPT_MODE, key);
      return cipher.doFinal(srcBytes);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }
}
