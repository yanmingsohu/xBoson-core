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
import com.xboson.chain.Block;
import com.xboson.chain.ISigner;
import com.xboson.chain.ISignerProvider;
import com.xboson.chain.ITypes;
import com.xboson.crypto.PublicKeyReader;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.security.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * app 应用区块链签名提供商, 使用平台数据库做签名架构.
 */
public class ChainSignerProvider implements ISignerProvider {

  private static final String SIGNER_ALGORITHM   = "SHA256withRSA";
  private static final String PRIVATE_KEY_PREFIX = "pri_";
  private static final String PUBLIC_KEY_PREFIX  = "pub_";
  private static final String SQL_FILE = "open_chain_key";


  @Override
  public ISigner getSigner(String chainName, String channelName) {
    if (Tool.isNulStr(chainName)) throw new XBosonException.BadParameter(
              "String chainName", "is null");
    if (Tool.isNulStr(channelName)) throw new XBosonException.BadParameter(
              "String channelName", "is null");

    return new Signer(chainName, channelName);
  }


  public static class Signer implements ISigner {
    private KeyPair[] keys;
    private String signer_algorithm;
    private String chain;
    private String channel;


    private Signer(String chainName, String channelName) {
      this.signer_algorithm = SIGNER_ALGORITHM;
      this.chain = chainName;
      this.channel = channelName;
      installKey();
    }


    private void installKey() {
      keys = new KeyPair[ITypes.LENGTH];

      String sql = SqlReader.read(SQL_FILE);
      SqlImpl sqlimpl = (SqlImpl) ModuleHandleContext._get("sql");
      Object[] parm = new Object[] { chain, channel };
      try {
        Iterator<ScriptObjectMirror> it = sqlimpl.queryStream(sql, parm).iterator();
        while (it.hasNext()) {
          ScriptObjectMirror row = it.next();
          //PublicKey pub = PublicKeyReader.class
        }
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }


    @Override
    public void sign(Block block) {
      try {
        Signature si = Signature.getInstance(signer_algorithm);
        si.initSign(keys[block.type].getPrivate());
        doFields(block, si);
        block.sign = si.sign();
      } catch (Exception e) {
        throw new XBosonException(e);
      }
    }


    @Override
    public boolean verify(Block block) {
      try {
        Signature si = Signature.getInstance(signer_algorithm);
        si.initVerify(keys[block.type].getPublic());
        doFields(block, si);
        return si.verify(block.sign);
      } catch (Exception e) {
        throw new XBosonException(e);
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
