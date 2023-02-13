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
// 文件创建日期: 18-7-17 上午8:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/Order.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.ChainEvent;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;

import java.security.KeyPair;


/**
 * 只有 0 号节点有排序服务
 */
public class Order extends AbsPeer implements IXRemote, IPeer {

  /**
   * 使用工厂来创建该类的实例, 而不是使用构造函数
   * @see PeerFactory#peer() 创建该类的实例
   */
  public Order() {
  }


  public byte[] sendBlock(String chainName, String channelName, BlockBasic b) {
    byte[] key = sendBlockLocal(chainName, channelName, b);
    sendNewBlock(chainName, channelName);
    return key;
  }


  public void createChannel(String chainName, String channelName,
                            String uid, String exp, KeyPair[] ks) {
    createChannelLocal(chainName, channelName, uid, exp, ks);
    sendNewChannel(chainName, channelName, exp, ks);
  }


  private void sendNewChannel(String chain, String channel, String exp, KeyPair[] ks) {
    ChainEvent e = new ChainEvent(chain, channel, exp, ks);
    GlobalEventBus.me().emit(Names.chain_sync, e, NEW_CHANNEL);
  }


  private void sendNewBlock(String chain, String channel) {
    ChainEvent e = new ChainEvent(chain, channel, null, null);
    GlobalEventBus.me().emit(Names.chain_sync, e, NEW_BLOCK);
  }
}
