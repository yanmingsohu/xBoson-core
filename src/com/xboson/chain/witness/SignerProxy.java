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

import com.xboson.been.XBosonException;
import com.xboson.util.Pipe;
import com.xboson.util.StreamRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;


/**
 * 远程签名者代理, 与一个远程见证者连接
 */
public class SignerProxy {

  private WitnessConnect wit;
  private PublicKey pk;


  public SignerProxy(PublicKey pk, WitnessConnect wit) {
    this.pk  = pk;
    this.wit = wit;
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


  public byte[] sign(InputStream i) throws IOException {
    return wit.doSign(new StreamRequestBody(i, WitnessConnect.BINARY));
  }


  public byte[] sign(Pipe.Context pc) {
    try {
      Pipe p = new Pipe(pc);
      return sign(p.openInputStream());
    } catch (IOException io) {
      throw new XBosonException.IOError(io);
    }
  }


  /**
   * @see WitnessFactory#verify(PublicKey, byte[], byte[])
   */
  public boolean verify(byte[] data, byte[] sign) {
    return WitnessFactory.verify(pk, data, sign);
  }
}
