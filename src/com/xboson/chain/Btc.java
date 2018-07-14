////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;


/**
 * 密钥对使用 DER 格式.
 */
public class Btc {

  private final static byte[] networkID = new byte[] {0, 0};
  private KeyPair kp;
  private String wallet;


  public byte[] publicKey() {
    try {
      return getKeyPair().getPublic().getEncoded();
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public byte[] privateKey() {
    try {
      return getKeyPair().getPrivate().getEncoded();
    } catch(Exception e) {
      throw new XBosonException(e);
    }
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


  private String walletImpl(byte[] publicKey) {
    byte[] sha256Bytes = Hash.sha256(publicKey);
    RIPEMD160Digest digest = new RIPEMD160Digest();
    digest.update(sha256Bytes, 0, sha256Bytes.length);
    byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
    digest.doFinal(ripemd160Bytes, 0);

    byte[] extendedRipemd160Bytes = Hash.add(networkID, ripemd160Bytes);
    byte[] twiceSha256Bytes = Hash.sha256(Hash.sha256(extendedRipemd160Bytes));
    byte[] checksum = new byte[4];
    System.arraycopy(twiceSha256Bytes, 0, checksum, 0, 4);
    byte[] binaryBitcoinAddressBytes = Hash.add(extendedRipemd160Bytes, checksum);

    return Base58Codec.doEncode(binaryBitcoinAddressBytes);
  }


  private KeyPair getKeyPair() throws NoSuchAlgorithmException,
          InvalidAlgorithmParameterException {
    if (kp == null) {
      synchronized (this) {
        if (kp == null) {
          KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
          ECGenParameterSpec ps = new ECGenParameterSpec("secp256k1");
          kpg.initialize(ps);
          kp = kpg.generateKeyPair();
        }
      }
    }
    return kp;
  }
}
