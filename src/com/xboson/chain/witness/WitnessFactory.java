////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-8-13 下午3:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/WitnessFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.been.Config;
import com.xboson.been.Witness;
import com.xboson.been.XBosonException;
import com.xboson.chain.Block;
import com.xboson.chain.ITypes;
import com.xboson.chain.SignNode;
import com.xboson.chain.VerifyException;
import com.xboson.db.SqlResult;
import com.xboson.db.sql.SqlReader;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.IBytesWriter;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;


public class WitnessFactory {

  public static final String SIGNER_ALGORITHM = "SHA256withECDSA";

  private static final Charset CS = IConstant.CHARSET;
  private static final String SQL_WITNESS = "open_witness";
  private static WitnessFactory instance;

  private Map<String, Witness> pool;
  private Config cf;
  private Log log;


  private WitnessFactory() {
    this.pool = new WeakHashMap<>();
    this.cf   = SysConfig.me().readConfig();
    this.log  = LogFactory.create("witness-factory");
  }


  public static WitnessFactory me() {
    if (instance == null) {
      synchronized (WitnessFactory.class) {
        if (instance == null) {
          instance = new WitnessFactory();
        }
      }
    }
    return instance;
  }


  public synchronized Witness get(String witness_id) {
    if (Tool.isNulStr(witness_id))
      throw new XBosonException.BadParameter("String witnessId", "null");

    Witness ret = pool.get(witness_id);
    if (ret != null)
      return ret;

    ret = getFromDB(witness_id);
    if (ret == null)
      throw new XBosonException.NotExist("Witness from id:"+ witness_id);

    pool.put(witness_id, ret);
    return ret;
  }


  /**
   * 更新缓存
   *
   * TODO: 当 Witness 主机连接改变时的集群消息
   */
  public synchronized void update(String witnessId) {
    pool.remove(witnessId);
  }


  private Witness getFromDB(String witnessId) {
    Object[] parm = { witnessId };

    try (SqlResult sr = SqlReader.query(SQL_WITNESS, cf.db, parm)) {
      ResultSet rs = sr.getResult();
      if (rs.next()) {
        return new Witness(witnessId,
                rs.getString("publickey"),
                rs.getString("urlperfix"),
                rs.getString("host"),
                rs.getString("algorithm"),
                rs.getInt("port"));
      }
      return null;
    } catch (SQLException e) {
      throw new XBosonException(e);
    }
  }


  public WitnessConnect openConnection(String witnessId) {
    Witness wit = get(witnessId);
    return new WitnessConnect(wit);
  }


  /**
   * 使用本地保存的见证者公钥验证数据块
   */
  public boolean consensusLocalVerify(Block b) {
    return false; //TODO 未完成
  }


  /**
   * 打开远程共识对象, 用于签名
   * //TODO 优化
   */
  public IConsensusDo openConsensusSign() {
    return new IConsensusDo() {
      public void log(Object... p) {
        log.info(p);
      }

      public boolean doAction(String witnessId, PublicKey key, Block b) {
        SignerProxy sp = new SignerProxy(key, openConnection(witnessId));
        byte[] signBytes = sp.sign((OutputStream out) -> {
          WitnessFactory.doFields(null, b, out);
        });
        b.pushSign(new SignNode(signBytes, witnessId));
        return true;
      }
    };
  }


  /**
   * 使用公钥验证签名正确性
   * @param data 原始数据
   * @param sign 数据的签名
   * @return 验证正确返回 true, 任何加密算法错误都会抛出异常.
   */
  public static boolean verify(PublicKey pk, byte[] data, byte[] sign) {
    try {
      Signature si = Signature.getInstance(SIGNER_ALGORITHM);
      si.initVerify(pk);
      si.update(data);
      return si.verify(sign);
    } catch (Exception e) {
      throw new VerifyException(e.getMessage());
    }
  }


  /**
   * 区块的签名算法
   */
  public static void doFields(KeyPair[] keys, Block block, Signature si) {
    doFields(keys, block, (byte[] b) -> {
      try {
        si.update(b);
      } catch (SignatureException e) {
        throw new XBosonException(e);
      }
    });
  }


  public static void doFields(KeyPair[] keys, Block block, OutputStream out) {
    doFields(keys, block, (byte[] bytes) -> {
      try {
        out.write(bytes);
        out.close();
      } catch (IOException e) {
        throw new XBosonException.IOError(e);
      }
    });
  }


  /**
   * 区块的输出算法
   * @param keys 平台默认密钥对, 可以为空
   * @param block 输出的区块
   * @param out 写出字节
   */
  public static void doFields(KeyPair[] keys, Block block, IBytesWriter out) {
    out.write(block.key);
    out.write(block.getData());
    out.write(block.getUserId().getBytes(CS));
    out.write(Long.toString(block.create.getTime()).getBytes(CS));

    switch (block.type) {
      case ITypes.CHAINCODE_CONTENT:
        out.write(block.getApiPath().getBytes(CS));
        out.write(block.getApiHash().getBytes(CS));
        break;

      case ITypes.GENESIS:
        for (int i=1; i<keys.length; ++i) {
          KeyPair kp = keys[i];
          //
          // 创世区块私钥离线后无法验证
          //
          if (i != ITypes.GENESIS) {
            out.write(kp.getPrivate().getEncoded());
          }
          out.write(kp.getPublic().getEncoded());
        }
        break;

      case ITypes.NORM_DATA:
      case ITypes.ENCRYPTION_DATA:
        out.write(block.getChaincodeKey());
        break;
    }
  }
}
