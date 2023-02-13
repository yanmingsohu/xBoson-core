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
public final class ApiEncryption implements IConstant {

  /**
   * 分组加密, 不可增减, 否则脚本将不可解密
   */
  private final static String[] z = {
          "U+vynW58DN6me+A2bzSc", "wKR9+F4Qr8t9caFHG0iS", "WCF7CErNJoQ6vCGdx62w",
          "mtCqVaqfmAQacsdIToxH", "k32bsUDqjO6virGvbUeq", "LiU38osNLhWV256aS5Qc",
          "in6vgWitlIn05D5gDlZd", "ZZXaGxkx1WOF6LAQjcsk", "egAMM+qHfOpI1ZTZ8kCu",
          "egAMM+qHfOpI1ZTZ8kCu", "tp+6PrEA5Bf7m3v9TCMi", "/jMBnB16ZZ5zodxqPtGQ",
          "MdA5Zuvq83lymwbTadT/", "yM4S8cKIcf5ZEEc7WV8C", "QZDVnPHOtN4dBrMida+s",
  };


  private final int ZLEN;
  private final SecretKeySpec[] zipkey;

  private final static int BUF_SIZE = 1024;
  private static AES2 ekey;
  public final static ApiEncryption me;


  static {
    me = new ApiEncryption();
    try {
      String code = "1200"; // 从配置文件读取
      String encode = Password.encodeSha256(code, "zr_zy秘");
      ekey = new AES2(code + encode);

    } catch(Exception e) {
      e.printStackTrace();
      System.exit(2);
    }
  }


  /**
   * 用密钥组, 构建加密算法
   */
  public ApiEncryption(String[] zipp) {
    ZLEN = zipp.length;
    zipkey = new SecretKeySpec[ZLEN];
    for (int i=0; i<ZLEN; ++i) {
      zipkey[i] = AES2.genKey(zipp[i].getBytes(CHARSET));
      zipp[i] = null;
    }
  }


  private ApiEncryption() {
    this(z);
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
  public String encryptApi2(String code, int zipi) {
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
  public byte[] decryptApi2(String code, int zipi) {
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
