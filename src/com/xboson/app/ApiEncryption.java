////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午8:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ApiEncryption.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.XBosonException;
import com.xboson.util.AES2;
import com.xboson.util.Password;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.c0nst.IConstant;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


/**
 * API 脚本加密/解密
 */
public class ApiEncryption implements IConstant {

  /**
   * 分组加密, 不可增减, 否则脚本将不可解密
   */
  private final static String[] zipp = {
        "U+vynW58DN6me+A2bzSc", "wKR9+F4Qr8t9caFHG0iS", "WCF7CErNJoQ6vCGdx62w",
        "mtCqVaqfmAQacsdIToxH", "k32bsUDqjO6virGvbUeq", "LiU38osNLhWV256aS5Qc",
        "in6vgWitlIn05D5gDlZd", "ZZXaGxkx1WOF6LAQjcsk", "egAMM+qHfOpI1ZTZ8kCu",
        "egAMM+qHfOpI1ZTZ8kCu", "tp+6PrEA5Bf7m3v9TCMi", "/jMBnB16ZZ5zodxqPtGQ",
        "MdA5Zuvq83lymwbTadT/", "yM4S8cKIcf5ZEEc7WV8C", "QZDVnPHOtN4dBrMida+s",
  };

  private final static int ZLEN = zipp.length;
  private final static SecretKeySpec[] zipkey = new SecretKeySpec[ZLEN];
  private final static int BUF_SIZE = 1024;
  private static AES2 ekey;


  static {
    try {
      String code = "1200"; // 从配置文件读取
      String encode = Password.encodeSha256(code, "zr_zy秘");
      ekey = new AES2(code + encode);

      for (int i=0; i<ZLEN; ++i) {
        zipkey[i] = AES2.genKey(zipp[i].getBytes(CHARSET));
        zipp[i] = null;
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(2);
    }
  }


  public static String encryptApi(String code) {
    return ekey.encrypt(code);
  }


  public static byte[] decryptApi(String mi) {
    return ekey.decrypt(mi);
  }


  /**
   * 加密压缩 api 脚本, zipi 码的不同使用不同的压缩密钥
   * @param code 源代码
   * @param zipi 压缩值可以是任意整数
   */
  public static String encryptApi2(String code, int zipi) {
    if (zipi == 0) return encryptApi(code);
    try {
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(ENCRYPT_MODE, zipkey[ (++zipi<0? -zipi: zipi)% ZLEN ]);

      StringBufferOutputStream out = new StringBufferOutputStream(code.length());
      OutputStream b64 = Base64.getEncoder().wrap(out);
      CipherOutputStream cs = new CipherOutputStream(b64, cipher);
      DeflaterOutputStream ds = new DeflaterOutputStream(cs);

      ds.write(code.getBytes(CHARSET));
      ds.close();
      return out.toString();
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 解压缩 api 脚本, zipi 码的不同使用不同的压缩密钥 (为提升效率返回 byte[])
   * @param code 已经加密的代码
   * @param zipi 压缩值可以是任意整数
   */
  public static byte[] decryptApi2(String code, int zipi) {
    if (zipi == 0) return decryptApi(code);
    try {
      Cipher cipher = Cipher.getInstance(AES_NAME);
      cipher.init(DECRYPT_MODE, zipkey[ (++zipi<0? -zipi: zipi)% ZLEN ]);

      ByteArrayInputStream orgin = new ByteArrayInputStream(code.getBytes(CHARSET));
      InputStream b64 = Base64.getDecoder().wrap(orgin);
      CipherInputStream cs = new CipherInputStream(b64, cipher);
      InflaterInputStream ds = new InflaterInputStream(cs);

      StringBufferOutputStream out = new StringBufferOutputStream(code.length());
      out.write(ds, true);
      return out.toBytes();
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }
}
