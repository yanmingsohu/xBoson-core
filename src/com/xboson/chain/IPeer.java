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
// 文件创建日期: 18-7-17 上午11:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/IPeer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.ChainEvent;
import com.xboson.rpc.IXRemote;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.PublicKey;


/**
 * 区块链高级接口, 与区块链网络中的节点连接进行操作.
 */
public interface IPeer extends Serializable, IXRemote {

  int NEW_CHANNEL   = 1;
  int NEW_BLOCK     = 2;


  /**
   * 发送区块到链
   * @param chainName 链名
   * @param channelName 通道名, 通道必须已经存在, 否则抛出异常
   * @param b 区块数据
   * @return 新区块的主键
   */
  byte[] sendBlock(String chainName, String channelName, BlockBasic b)
          throws RemoteException;


  /**
   *
   * 创建新通道
   * @param chainName 链名, 链会自动创建
   * @param channelName 通道名, 如果通道已经存在会抛出异常
   * @param userid 用户id
   * @param consensusExp 共识表达式
   * @param keys 系统密钥对数组
   * @throws RemoteException
   */
  void createChannel(String chainName, String channelName,
                     String userid, String consensusExp,
                     KeyPair[] keys)
          throws RemoteException;


  /**
   * 查询链上的一个块, 找不到区块返回 null, 如果验证失败抛出 VerifyException.
   */
  Block search(String chainName, String channelName, byte[] key)
          throws RemoteException;


  /**
   * 返回世界状态, 如果返回空数组说明是创世区块
   */
  byte[] worldState(String chainName, String channelName)
          throws RemoteException;


  /**
   * 返回最后区块的 key, 如果返回空数组说明是创世区块
   */
  byte[] lastBlockKey(String chainName, String channelName)
          throws RemoteException;


  /**
   * 返回创世区块的 key
   */
  byte[] genesisKey(String chain, String channel)
          throws RemoteException;


  /**
   * 返回系统中所有的区块链名称
   */
  String[] allChainNames() throws RemoteException;


  /**
   * 返回链上的所有通道名字
   */
  String[] allChannelNames(String chain) throws RemoteException;


  /**
   * 返回所有区块链上的配置
   */
  ChainEvent[] allChainSetting() throws RemoteException;


  /**
   * 链/通道已经存在返回 true
   */
  boolean channelExists(String chain, String channel) throws RemoteException;


  /**
   * 返回链码块的 key, 如果不存在返回 null
   */
  byte[] getChaincodeKey(String chain, String channel, String path, String hash)
          throws RemoteException;


  /**
   * 返回区块链长度
   */
  int size(String chain, String channel) throws RemoteException;


  /**
   * 返回链的见证者公钥, 如果见证者不用于链的签名(不存在于共识表达式中) 返回 null.
   */
  PublicKey getWitnessPublicKey(String chain, String channel, String wid)
          throws RemoteException;

}
