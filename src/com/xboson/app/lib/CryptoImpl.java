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
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import com.xboson.util.Hash;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;


public class CryptoImpl extends RuntimeUnitImpl {

  private static Map<String, CryptoFact> impl;

  static {
    impl = new HashMap<>();
    put(new AES());
    put(new DES());
    put(new PBE());
    put(new IDEA());
    put(new AES_CBC_PKCS5Padding());
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
    return new CipherJs( cf.cipher(Cipher.ENCRYPT_MODE, key) );
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
    return new CipherJs( cf.cipher(Cipher.DECRYPT_MODE, key) );
  }


  public CipherJs createDecipher(String algorithm, Bytes pass, Bytes iv) throws Exception {
    CryptoFact cf = get_cipher(algorithm);
    SecretKey key = cf.getKey(pass.bin());
    IvParameterSpec ivSpec = new IvParameterSpec(iv.bin());
    return new CipherJs( cf.cipher(Cipher.DECRYPT_MODE, key, ivSpec) );
  }


  public Bytes randomBytes(int len) throws NoSuchAlgorithmException {
    byte[] b = new byte[len];
    SecureRandom rand = SecureRandom.getInstanceStrong();
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


  /**
   * @see Digest
   */
  public Digest.HashWarp createHash(String algorithm) throws Exception {
    return new Digest.HashWarp(algorithm);
  }


  private CryptoFact get_cipher(String alg) throws Exception {
    alg = alg.toLowerCase();
    CryptoFact cf = impl.get(alg);
    if (cf == null) {
      throw new XBosonException("Unknow algorithm "+ alg);
    }
    return cf;
  }


  //
  // 加密工厂抽象基类
  //
  private static abstract class CryptoFact {
    abstract Cipher cipher(int mode, SecretKey key, AlgorithmParameterSpec ps) throws Exception;
    abstract SecretKey getKey(byte[] password) throws Exception;
    abstract String name();

    // 使用字符串的字节表示密码
    SecretKey getKey(String password) throws Exception {
      return getKey(password.getBytes(IConstant.CHARSET));
    }

    // AlgorithmParameterSpec 参数为 null
    Cipher cipher(int mode, SecretKey key) throws Exception {
      return cipher(mode, key, null);
    }
  }


  private static class AES extends CryptoFact {
    String name() {
      return "aes";
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
      return "des";
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
      return "pbe";
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
      return "idea";
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
