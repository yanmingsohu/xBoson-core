////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-13 下午5:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/Hash.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class Hash {

  private final MessageDigest md;


  public Hash() {
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new XBosonException(e);
    }
  }


  public void update(String s) {
    md.update(s.getBytes(IConstant.CHARSET));
  }


  public void update(byte[] b) {
    md.update(b);
  }


  public void update(Date d) {
    update(d.getTime());
  }


  public void update(long l) {
    md.update((byte) (l & 0xFF));
    md.update((byte) ((l>>8 ) & 0xFF));
    md.update((byte) ((l>>16) & 0xFF));
    md.update((byte) ((l>>24) & 0xFF));

    md.update((byte) ((l>>32) & 0xFF));
    md.update((byte) ((l>>40) & 0xFF));
    md.update((byte) ((l>>48) & 0xFF));
    md.update((byte) ((l>>56) & 0xFF));
  }


  public void update(int l) {
    md.update((byte) (l & 0xFF));
    md.update((byte) ((l>>8 ) & 0xFF));
    md.update((byte) ((l>>16) & 0xFF));
    md.update((byte) ((l>>24) & 0xFF));
  }


  public byte[] digest() {
    return md.digest();
  }


  public static byte[] sha256(byte[] i) {
    Hash h = new Hash();
    h.update(i);
    return h.digest();
  }


  public static byte[] add(byte[] a, byte[] b) {
    byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }
}
