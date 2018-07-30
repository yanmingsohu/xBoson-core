////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-30 下午5:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/service/Witness.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.chain.Btc;
import com.xboson.chain.witness.SignerProxy;
import com.xboson.chain.witness.WitnessConnect;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import okhttp3.*;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;


@XPath("/witness")
public class Witness extends XService implements IConstant {

  private static final String ERRMSG = "cannot found service";
  private static final String ALGORITHM = "SHA256withECDSA";
  private static final int GEN_RAND_DATA_LEN = 256;

  private static final MediaType BINARY
          = MediaType.parse("application/octet-stream");

  private OkHttpClient hc;


  @Override
  public void service(CallData data) throws Exception {
    subService(data, ERRMSG);
  }


  public void register(CallData data) throws Exception {
    String algorithm = data.getString("algorithm", 1, ALGORITHM.length());
    if (!ALGORITHM.equals(algorithm)) {
      data.xres.responseMsg("bad algorithm value", 2);
      return;
    }

    String pubkey = data.getString("publickey", 1, 999);
    String host   = data.getString("host", 1, 128);
    int port      = data.getInt("port", 1, 65535);
    String prefix = data.getString("urlperfix", 0, 128);

    byte[] rand = Tool.randomBytes(GEN_RAND_DATA_LEN);
    WitnessConnect wc = new WitnessConnect(host, port, prefix);
    PublicKey pk = Btc.publicKey(Hex.Names.BASE64, pubkey);
    SignerProxy sp = new SignerProxy(pk, wc);

    try {
      byte[] sign = sp.sign(rand);
      if (! sp.verify(rand, sign)) {
        data.xres.responseMsg("Signature verification failed", 3);
        return;
      }

      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 插入数据库
      // 设计一个 见证者块, 并将块插入区块链, 非创世区块和见证者块 使用见证者签名

    } catch (IOException e) {
      data.xres.responseMsg("Network unreachable: "+ host+ ':' +port, 4);

    } catch (XBosonException e) {
      data.xres.responseMsg("Error: "+ e.getMessage(), 5);
    }
  }


  public void change(CallData data) throws Exception {}



  @Override
  public boolean needLogin() {
    return false;
  }


  @Override
  public String logName() {
    return "bc-witness-service";
  }
}
