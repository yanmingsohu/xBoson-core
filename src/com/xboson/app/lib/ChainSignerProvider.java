////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-19 下午12:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ChainSignerProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.chain.*;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.security.*;
import java.util.Iterator;


/**
 * app 应用区块链签名提供商, 使用平台数据库做签名架构.
 * @see com.xboson.chain.Btc 公钥/私钥生成算法
 */
public class ChainSignerProvider implements ISignerProvider {

  private static final String SIGNER_ALGORITHM = "SHA256withRSA";
  private static final String SQL_FILE = "open_chain_key";


  @Override
  public ISigner getSigner(String chainName, String channelName) {
    if (Tool.isNulStr(chainName)) throw new XBosonException.BadParameter(
              "String chainName", "is null");
    if (Tool.isNulStr(channelName)) throw new XBosonException.BadParameter(
              "String channelName", "is null");

    KeyPair[] kp = openChainKeys(chainName, channelName);
    return new Signer(chainName, channelName, kp);
  }


  /**
   * 在脚本环境中调用该方法
   */
  private KeyPair[] openChainKeys(String chain, String channel) {
    KeyPair[] keys = new KeyPair[ ITypes.LENGTH +1 ];

    String sql = SqlReader.read(SQL_FILE);
    SqlImpl sqlimpl = (SqlImpl) ModuleHandleContext._get("sql");
    Object[] parm = { chain, channel };

    try (QueryImpl.ResultReader rr = sqlimpl.queryStream(sql, parm)) {
      Iterator<ScriptObjectMirror> it = rr.iterator();
      while (it.hasNext()) {
        ScriptObjectMirror row = it.next();
        PublicKey  pub = Btc.publicKey(row.get("publickey").toString());
        PrivateKey pri = Btc.privateKey(row.get("privatekey").toString());
        int index = (int) row.get("type");
        keys[index] = new KeyPair(pub, pri);
      }
      return keys;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public static class Signer implements ISigner {
    private KeyPair[] keys;
    private String signer_algorithm;
    private String chain;
    private String channel;


    private Signer(String chainName, String channelName, KeyPair[] keys) {
      this.signer_algorithm = SIGNER_ALGORITHM;
      this.chain    = chainName;
      this.channel  = channelName;
      this.keys     = keys;
    }


    @Override
    public void sign(Block block) {
      try {
        Signature si = Signature.getInstance(signer_algorithm);
        KeyPair pair = getKeyPair(block.type);
        si.initSign(pair.getPrivate());
        if (block.type == ITypes.GENESIS) {
          doGenesis(block, si);
        } else {
          doFields(block, si);
        }
        block.sign = si.sign();
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }


    @Override
    public boolean verify(Block block) {
      try {
        Signature si = Signature.getInstance(signer_algorithm);
        KeyPair pair = getKeyPair(block.type);
        si.initVerify(pair.getPublic());
        if (block.type == ITypes.GENESIS) {
          doGenesis(block, si);
        } else {
          doFields(block, si);
        }
        return si.verify(block.sign);
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }


    private KeyPair getKeyPair(int i) {
      KeyPair pair = keys[i];
      if (pair == null) {
        throw new NullPointerException("cannot found KeyPair index:"+ i);
      }
      return pair;
    }


    private void doGenesis(Block b, Signature si) throws SignatureException {
      doFields(b, si);
      for (int i=0; i<keys.length; ++i) {
        KeyPair kp = keys[i];
        if (kp != null) {
          si.update(kp.getPrivate().getEncoded());
          si.update(kp.getPublic().getEncoded());
        }
      }
    }


    private void doFields(Block block, Signature si) throws SignatureException {
      si.update(block.key);
      si.update(block.getData());
      si.update(block.getApiPath().getBytes(IConstant.CHARSET));
      si.update(block.getApiHash().getBytes(IConstant.CHARSET));
      si.update(block.getUserId().getBytes(IConstant.CHARSET));
    }
  }
}
