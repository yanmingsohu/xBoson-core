////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午8:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/IConstant.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.db.IDict;

import javax.crypto.Cipher;
import java.nio.charset.Charset;


/**
 * 编程用常量
 */
public interface IConstant extends IDict {

  /**
   * 尽可能不要直接使用, 而是用 CHARSET
   */
  String CHARSET_NAME = "UTF-8";

  /**
   * 全局编码
   */
  Charset CHARSET = Charset.forName(CHARSET_NAME);

  /**
   * 常用加密/摘要算法名字
   */
  String MD5_NAME       = "MD5";
  String SHA256_NAME    = "SHA-256";
  String AES_NAME       = "AES";
  String SHA1_PRNG_NAME = "SHA1PRNG";
  String PBK1_NAME      = "PBKDF2WithHmacSHA1";
  String AES_C_P_NAME   = "AES/CBC/PKCS5Padding";

  int DECRYPT_MODE = Cipher.DECRYPT_MODE;
  int ENCRYPT_MODE = Cipher.ENCRYPT_MODE;
}