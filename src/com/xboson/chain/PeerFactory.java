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
// 文件创建日期: 18-7-17 下午12:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/PeerFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.XBosonException;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;


public class PeerFactory extends OnExitHandle implements IConstant {

  private static final String RPC_NAME   = "XB.rpc.blockchain.peer";
  private static final String ORDER_NODE = "0";

  private static PeerFactory instance;

  private final Log log;
  private final boolean enable;
  private IPeer peer;


  private PeerFactory() {
    this.log    = LogFactory.create("chain-peer-factory");
    this.enable = SysConfig.me().readConfig().chainEnable;

    if (!enable)
      return;

    modeFixOrderNode();
    // modeRaft();
  }


  /**
   * Raft 算法, 无单点失效问题.
   * <a href="https://raft.github.io/">Raft 可视化</a>
   */
  private void modeRaft() {
    // TODO 完成 raft 算法
  }


  /**
   * 固定 0 号节点为主节点, 如果该节点失效, 区块链服务不可用
   */
  private void modeFixOrderNode() {
    String nodeid = ClusterManager.me().localNodeID();
    boolean is_order = ORDER_NODE.equals(nodeid);
    if (is_order) {
      peer = new Order();
      RpcFactory.me().bind(peer, RPC_NAME);
    } else {
      peer = new Peer(() -> {
        return (IPeer) RpcFactory.me().lookup(ORDER_NODE, RPC_NAME);
      });
    }

    if (peer instanceof IPeerLocal) {
      IPeerLocal pl = (IPeerLocal) peer;
      installSigner(pl);
      pl.startSynchronizeThread();
    }
  }


  @Override
  protected void exit() {
    if (peer instanceof IPeerLocal) {
      ((IPeerLocal) peer).waitOver();
    }
    peer = null;
  }


  public static PeerFactory me() {
    if (instance == null) {
      synchronized (PeerFactory.class) {
        if (instance == null) {
          instance = new PeerFactory();
        }
      }
    }
    return instance;
  }


  public IPeer peer() {
    if (peer == null) {
      throw new XBosonException.Closed("Blockchain server");
    }
    return peer;
  }


  /**
   * 安装签名提供商
   * @see IConstant#CHAIN_SIGNER_PROVIDER 签名提供商来源
   */
  private void installSigner(IPeerLocal pl) {
    try {
      Class<? extends ISignerProvider> ps = CHAIN_SIGNER_PROVIDER;
      ISignerProvider sp = ps.newInstance();
      pl.registerSignerProvider(sp);
    } catch(Exception e) {
      log.error("install signer", e);
    }
  }
}
