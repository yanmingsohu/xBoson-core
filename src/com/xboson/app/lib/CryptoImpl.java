////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-8-17 上午9:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/CryptoImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.chain.Btc;
import com.xboson.script.IVisitByScript;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import com.xboson.util.Hash;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;


public class CryptoImpl extends RuntimeUnitImpl implements IVisitByScript {

  private static Map<String, CryptoFact> impl;

  static {
    impl = new HashMap<>();
    put(new AES());
    put(new DES());
    put(new PBE());
    put(new IDEA());
    put(new CommonImpl("AES/CBC/PKCS5Padding"));
    put(new CommonImpl("AES/CBC/NoPadding"));
    put(new SM2crc123());
    put(new SM2crc132());
    put(new CommonImpl("SM4/ECB/PKCS5Padding"));
    put(new CommonImpl("SM4/ECB/NoPadding"));
    put(new CommonImpl("SM4/CBC/PKCS5Padding"));
    put(new CommonImpl("SM4/CBC/NoPadding"));
  }

  private static void put(CryptoFact cf) {
    impl.put(cf.name().toLowerCase(), cf);
  }


  public CryptoImpl() {
    super(null);
  }


  public String[] algorithmNames() {
    String[] n = new String[impl.size()];
    int i = 0;
    for (CryptoFact cf : impl.values()) {
      n[i++] = cf.name();
    }
    return n;
  }


  public CipherJs createCipher(String algorithm, String pass) throws Exception {
    CryptoFact cf = get_cipher(algorithm);
    SecretKey key = cf.getKey(pass);
    return new CipherJs( cf.cipher(Cipher.ENCRYPT_MODE, key, null) );
  }


  public CipherJs createCipher(String algorithm, Bytes pass, Bytes iv) throws Exception {
    CryptoFact cf = get_cipher(algorithm);
    SecretKey key = cf.getKey(pass.bin());
    IvParameterSpec ivSpec = new IvParameterSpec(iv.bin());
    return new CipherJs( cf.cipher(Cipher.ENCRYPT_MODE, key, ivSpec) );
  }


  public CipherJs createDecipher(String algorithm, String pass) throws Exception {
    CryptoFact cf = get_cipher(algorithm);
    SecretKey key = cf.getKey(pass);
    return new CipherJs( cf.cipher(Cipher.DECRYPT_MODE, key, null) );
  }


  public CipherJs createDecipher(String algorithm, Bytes pass, Bytes iv) throws Exception {
    CryptoFact cf = get_cipher(algorithm);
    SecretKey key = cf.getKey(pass.bin());
    IvParameterSpec ivSpec = new IvParameterSpec(iv.bin());
    return new CipherJs( cf.cipher(Cipher.DECRYPT_MODE, key, ivSpec) );
  }


  public Bytes randomBytes(int len) throws NoSuchAlgorithmException {
    byte[] b = new byte[len];
    // getInstanceStrong 在 linux 上会等待 /dev/random 直到有足够得'熵'后返回,
    // 这会导致阻塞.
    // SecureRandom rand = SecureRandom.getInstanceStrong();
    SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
    rand.nextBytes(b);
    return new Bytes(b);
  }


  public Bytes generateAesIV() throws NoSuchAlgorithmException {
    return randomBytes(16);
  }


  public Bytes generateAesPass(String pass) {
    Hash h = new Hash();
    h.update(pass);
    return new Bytes(h.digest());
  }


  public Bytes generateSm4Pass(String pass) {
    Hash h = new Hash("MD5");
    h.update(pass);
    return new Bytes(h.digest());
  }


  public Chain.KeyPairJs ECKeyPair() {
    return new Chain.KeyPairJs();
  }


  /**
   * @see Digest
   */
  public Digest.HashWarp createHash(String algorithm) throws Exception {
    return new Digest.HashWarp(algorithm);
  }


  private CryptoFact get_cipher(String alg) throws Exception {
    CryptoFact cf = impl.get(alg.toLowerCase());
    if (cf == null) {
      throw new XBosonException("Unknow algorithm "+ alg);
    }
    return cf;
  }


  //
  // 简易加密工厂抽象基类, 子类通常是对多个算法的组合
  //
  private static abstract class CryptoFact {
    abstract Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception;
    abstract SecretKey getKey(byte[] password) throws Exception;
    abstract String name();

    // 使用字符串的字节表示密码
    SecretKey getKey(String password) throws Exception {
      return getKey(password.getBytes(IConstant.CHARSET));
    }
  }


  private static class AES extends CryptoFact {
    String name() {
      return "AES";
    }

    SecretKey getKey(byte[] pass) throws Exception {
      KeyGenerator kgen = KeyGenerator.getInstance("AES");
      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(pass);
      kgen.init(128, secureRandom);
      return kgen.generateKey();
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(mode, key);
      return cipher;
    }
  }


  private static class DES extends CryptoFact {
    String name() {
      return "DES";
    }

    SecretKey getKey(byte[] pass) throws Exception {
      DESKeySpec desKey = new DESKeySpec(pass);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      return keyFactory.generateSecret(desKey);
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception {
      SecureRandom random = new SecureRandom();
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(mode, key, random);
      return cipher;
    }
  }


  private static class PBE extends CryptoFact {
    String name() {
      return "PBE";
    }

    SecretKey getKey(String pass) throws Exception {
      PBEKeySpec pbeKeySpec = new PBEKeySpec(pass.toCharArray());
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
      return factory.generateSecret(pbeKeySpec);
    }

    SecretKey getKey(byte[] pass) throws Exception {
      throw new XBosonException.NotImplements("password with byte[]");
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception {
      byte[] salt = Tool.randomBytes(8);
      PBEParameterSpec spec = new PBEParameterSpec(salt, 100);
      Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
      cipher.init(mode, key, spec);
      return cipher;
    }
  }


  private static class IDEA extends CryptoFact {
    String name() {
      return "IDEA";
    }

    SecretKey getKey(byte[] pass) throws Exception {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("IDEA");
      SecureRandom idea_rand = SecureRandom.getInstanceStrong();
      idea_rand.setSeed(pass);
      keyGenerator.init(128, idea_rand);
      return keyGenerator.generateKey();
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception {
      Cipher cipher = Cipher.getInstance("IDEA/ECB/ISO10126Padding");
      cipher.init(mode, key);
      return cipher;
    }
  }


  /**
   * @deprecated 由 CommonImpl 替代
   */
  private static class AES_CBC_PKCS5Padding extends CryptoFact {
    String name() {
      return "AES/CBC/PKCS5Padding";
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec iv) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(mode, key, iv);
      return cipher;
    }

    SecretKey getKey(byte[] pass) throws Exception {
      return new SecretKeySpec(pass, "AES");
    }
  }


  private static class SM2crc123 extends CryptoFact {
    Cipher newSM2() throws Exception {
      return Cipher.getInstance("SM2");
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception {
      Cipher cipher = newSM2();
      GMCipherSpi.SM2 a;
      org.bouncycastle.jcajce.provider.asymmetric.GM c;
      if (mode == Cipher.ENCRYPT_MODE) {
        PublicKey pkey = Btc.publicKey(key.toString());
        cipher.init(mode, pkey);
      } else {
        PrivateKey pkey = Btc.privateKey(key.toString());
        cipher.init(mode, pkey);
      }
      return cipher;
    }

    SecretKey getKey(byte[] password) throws Exception {
      return null; // yes null
    }

    String name() {
      return "SM2C1C2C3";
    }

    SecretKey getKey(String password) throws Exception {
      return new StringKey(password);
    }
  }


  private static class SM2crc132 extends SM2crc123 {
    String name() {
      return "SM2C1C3C2";
    }

    Cipher newSM2() throws Exception {
      return Cipher.getInstance("SM2C132");
    }
  }


  private static class StringKey implements SecretKey {
    String val;

    StringKey(String k) {
      val = k;
    }

    public String getAlgorithm() {
      throw new XBosonException("no algorithm");
    }

    public String getFormat() {
      return null;
    }

    public byte[] getEncoded() {
      return val.getBytes();
    }

    public String toString() {
      return val;
    }
  }


  private static class CommonImpl extends CryptoFact {
    private final String algorithm;
    private final String keyalg;

    CommonImpl(String aname) {
      algorithm = aname;
      keyalg = aname.split("/", 2)[0];
    }

    Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec iv) throws Exception {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(mode, key, iv);
      return cipher;
    }

    SecretKey getKey(byte[] password) throws Exception {
      return new SecretKeySpec(password, keyalg);
    }

    String name() {
      return algorithm;
    }
  }


  /**
   * 加密/解密导出类
   */
  public class CipherJs {
    private Cipher c;

    private CipherJs(Cipher c) {
      this.c = c;
    }

    public Bytes update(String str) {
      byte[] b = c.update(str.getBytes(IConstant.CHARSET));
      return b == null ? new Bytes() : new Bytes(b);
    }

    public Bytes update(Bytes bin) {
      byte[] b = c.update(bin.bin());
      return b == null ? new Bytes() : new Bytes(b);
    }

    public Bytes update(Buffer.JsBuffer buf) {
      byte[] b = c.update(buf._buffer().array());
      return b == null ? new Bytes() : new Bytes(b);
    }

    public Bytes end() throws BadPaddingException, IllegalBlockSizeException {
      byte[] b = c.doFinal();
      return b == null ? new Bytes() : new Bytes(b);
    }
  }
}
