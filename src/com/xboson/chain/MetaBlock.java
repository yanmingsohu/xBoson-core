/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 18-7-13 下午12:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/MetaBlock.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.util.Hex;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;

import java.io.Serializable;
import java.security.KeyPair;
import java.util.Date;


public class MetaBlock implements Serializable, ITypes {

  /** 最后块的主键 */
  public byte[] lastBlockKey;
  /** 世界状态 hash */
  public byte[] worldStateHash;
  /** 通道名称 */
  public String channelName;
  /** 创世区块主键 */
  public byte[] genesisKey;
  /** 共识表达式 */
  public String consensusExp;
  /** 系统默认密钥对 */
  public KeyPair[] keys;


  /**
   * 该构造函数用于在程序中创建新的 MetaBlock
   */
  public MetaBlock(String channelName, ISigner si) {
    this.channelName    = channelName;
    this.consensusExp   = si.getConsensusExp();
    this.keys           = si.getKeyPairs();
    this.lastBlockKey   = new byte[] {};
    this.worldStateHash = new byte[] {};
    this.genesisKey     = new byte[] {};
  }


  /**
   * 用随机数据生成创世区块
   */
  public static BlockBasic createGenesis() {
    return createGenesis(Tool.randomBytes(2048));
  }


  /**
   * 将 obj 序列化后作为创世区块数据
   */
  public static BlockBasic createGenesis(Object obj) {
    return createGenesis(Hex.toBytesWithoutErr(obj));
  }


  /**
   * 创建创世块
   */
  public static BlockBasic createGenesis(byte[] data) {
    BlockBasic b = new BlockBasic();
    b.setData(data);
    b.userid  = IConstant.NULL_STR;
    b.apiHash = IConstant.NULL_STR;
    b.apiPath = IConstant.NULL_STR;
    b.type    = GENESIS;
    b.chaincodeKey = new byte[0];
    return b;
  }

}
