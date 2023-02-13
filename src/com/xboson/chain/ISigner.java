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
// 文件创建日期: 18-7-17 上午8:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/ISigner.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;


/**
 * 签名者对象必须可以序列化, 不能是非静态内部类, 签名对象将被保存在链上.
 * 签名/验证方法应该对称, 不要在验证时超出签名的范围.
 *
 * <a href='https://www.ibm.com/developerworks/cn/java/j-lo-serial/index.html'>
 *   Java 序列化的高级认识</a>
 */
public interface ISigner extends Serializable {

  /**
   * 签名区块, 不同的区块类型签名方法不同;
   * 签名时, hash 为空
   */
  void sign(Block block);


  /**
   * 验证区块, 不同的区块类型, 验证方法不同; block 永远不为 null.
   * @return 验证成功返回 true, 否则返回 false.
   */
  boolean verify(Block block);


  /**
   * 将区块发送给见证者
   * @param block
   */
  void deliver(Block block);


  /**
   * 返回见证者的公钥, 该见证者必须存在于当前签名者中否则返回 null.
   */
  PublicKey getWitnessPublicKey(String wid);


  /**
   * 创世区块私钥仅在初始化区块链时用到一次,
   * 删除创世区块私钥可保证安全, 防止重构链数据.
   */
  default void removeGenesisPrivateKey() {}


  /**
   * 返回共识表达式原始字符串
   */
  default String getConsensusExp() { return null; }


  /**
   * 返回系统密钥对
   */
  default KeyPair[] getKeyPairs() { return null; }

}
