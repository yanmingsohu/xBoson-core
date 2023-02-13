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
// 文件创建日期: 18-7-14 上午11:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/Btc.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.XBosonException;
import com.xboson.util.Hash;
import com.xboson.util.Hex;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;


/**
 * 密钥对使用 DER 格式.
 * 字符串编码使用 base58.
 */
public class Btc {

  private final static byte[] networkID = new byte[] {0, 0};
  public final static String KEY_GEN_ALGORITHM = "EC";
  public final static String EC_KEY_PARM = "secp256k1";

  private KeyPair kp;
  private String wallet;


  public byte[] publicKey() {
    try {
      return getKeyPair().getPublic().getEncoded();
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 返回编码后的公钥
   */
  public String publicKeyStr() {
    return Base58.encode(publicKey());
  }


  public byte[] privateKey() {
    try {
      return getKeyPair().getPrivate().getEncoded();
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 返回编码后的私钥
   */
  public String privateKeyStr() {
    return Base58.encode(privateKey());
  }


  public String wallet() {
    if (wallet == null) {
      synchronized (this) {
        if (wallet == null) {
          wallet = walletImpl(publicKey());
        }
      }
    }
    return wallet;
  }


  /**
   * 将编码公钥还原为公钥对象
   */
  public static PublicKey publicKey(String base58str) {
    return publicKey(Hex.Names.BASE58, base58str);
  }


  /**
   * 使用指定的编码方式解码 str 并编译为公钥
   * @see Hex.Names 可用编码名称
   */
  public static PublicKey publicKey(String coding, String str) {
    try {
      byte[] buf = Hex.decode(coding, str);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(buf);
      KeyFactory kFactory = KeyFactory.getInstance(KEY_GEN_ALGORITHM);
      return kFactory.generatePublic(spec);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 将编码私钥还原为私钥对象
   */
  public static PrivateKey privateKey(String base58str) {
    return privateKey(Hex.Names.BASE58, base58str);
  }


  /**
   * 使用指定的编码方式解码 str 并编译为私钥
   * @see Hex.Names 可用编码名称
   */
  public static PrivateKey privateKey(String coding, String str) {
    try {
      byte[] buf = Hex.decode(coding, str);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(buf);
      KeyFactory kFactory = KeyFactory.getInstance(KEY_GEN_ALGORITHM);
      return kFactory.generatePrivate(spec);
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }



  private String walletImpl(byte[] publicKey) {
    byte[] sha256Bytes = Hash.sha256(publicKey);
    RIPEMD160Digest digest = new RIPEMD160Digest();
    digest.update(sha256Bytes, 0, sha256Bytes.length);
    byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
    digest.doFinal(ripemd160Bytes, 0);

    byte[] extendedRipemd160Bytes = Hash.join(networkID, ripemd160Bytes);
    byte[] twiceSha256Bytes = Hash.sha256(Hash.sha256(extendedRipemd160Bytes));
    byte[] checksum = new byte[4];
    System.arraycopy(twiceSha256Bytes, 0, checksum, 0, 4);
    byte[] bitcoinAddressBytes = Hash.join(extendedRipemd160Bytes, checksum);

    return Base58.encode(bitcoinAddressBytes);
  }


  /**
   * 随机生成密钥对
   */
  public KeyPair getKeyPair() {
    if (kp == null) {
      synchronized (this) {
        if (kp == null) {
          kp = genRandomKeyPair();
        }
      }
    }
    return kp;
  }


  public static KeyPair genRandomKeyPair() {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_GEN_ALGORITHM);
      ECGenParameterSpec ps = new ECGenParameterSpec(EC_KEY_PARM);
      kpg.initialize(ps);
      return kpg.generateKeyPair();
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }
}
