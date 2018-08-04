////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-30 下午7:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/SignerProxy.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.chain.Block;
import com.xboson.chain.ISigner;
import com.xboson.chain.VerifyException;

import java.io.IOException;
import java.security.*;


/**
 * 远程签名者代理
 */
public class SignerProxy implements ISigner {

  public static final String SIGNER_ALGORITHM = "SHA256withECDSA";

  private WitnessConnect wit;
  private PublicKey pk;


  public SignerProxy(PublicKey pk, WitnessConnect wit) {
    this.pk  = pk;
    this.wit = wit;
  }


  @Override
  public void sign(Block block) {
    throw new UnsupportedOperationException();
  }


  @Override
  public boolean verify(Block block) {
    throw new UnsupportedOperationException();
  }


  /**
   *
   * 使用远程节点(私钥)签名数据
   * @param data 待签名数据
   * @return 数据的签名
   * @throws IOException 连接远程节点失败
   */
  public byte[] sign(byte[] data) throws IOException {
    return wit.doSign(data);
  }


  /**
   * 使用公钥验证签名正确性
   * @param data 原始数据
   * @param sign 数据的签名
   * @return 验证正确返回 true, 任何加密算法错误都会抛出异常.
   */
  public boolean verify(byte[] data, byte[] sign) {
    try {
      Signature si = Signature.getInstance(SIGNER_ALGORITHM);
      si.initVerify(pk);
      si.update(data);
      return si.verify(sign);
    } catch (Exception e) {
      throw new VerifyException(e.getMessage());
    }
  }
}
