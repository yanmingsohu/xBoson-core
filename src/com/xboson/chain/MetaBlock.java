////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-13 下午12:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/MetaBlock.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;

import java.io.Serializable;


public class MetaBlock implements Serializable, ITypes {

  /** 最后块的主键 */
  public byte[] lastBlockKey;
  /** 世界状态 hash */
  public byte[] worldStateHash;
  /** 通道名称 */
  public String channelName;
  /** 创世区块主键 */
  public byte[] genesisKey;
  /** 最新的链码 hash */
  public String apiHash;
  /** 最新的链码路径 */
  public String apiPath;


  /**
   * 该构造被 Map 对象调用, 用于还原 MetaBlock
   */
  public MetaBlock() {
  }


  /**
   * 该构造函数用于在程序中创建新的 MetaBlock
   */
  public MetaBlock(String channelName) {
    this.channelName    = channelName;
    this.lastBlockKey   = new byte[] {};
    this.worldStateHash = new byte[] {};
    this.genesisKey     = new byte[] {};
  }


  /**
   * 用随机数据生成创世区块
   */
  public static Block createGenesis() {
    return createGenesis(Tool.randomBytes(99));
  }


  /**
   * 创建创世块
   */
  public static Block createGenesis(byte[] data) {
    Block b = new Block();
    b.setData(data);
    b.userid  = IConstant.NULL_STR;
    b.apiHash = IConstant.NULL_STR;
    b.apiPath = IConstant.NULL_STR;
    b.type    = GENESIS;
    return b;
  }

}
