////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-17 上午8:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/Peer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.JsonHelper;
import com.xboson.event.EventLoop;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.Hex;
import com.xboson.util.LocalLock;

import javax.naming.event.NamingEvent;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;


/**
 * 非 0 号节点都是账本节点, 可以查询, 数据同步依赖排序服务
 */
public class Peer extends AbsPeer {

  private final Log log;
  private ChainSyncListener csl;
  private IPeer order;


  /**
   * 使用工厂来创建该类的实例, 而不是使用构造函数
   * @see PeerFactory#peer() 创建该类的实例
   */
  public Peer(String orderClusterNodeID) {
    this.log   = LogFactory.create("chain-peer");
    this.order = (IPeer) RpcFactory.me().lookup(orderClusterNodeID, RPC_NAME);
    this.csl   = new ChainSyncListener();
  }


  public byte[] sendBlock(String chainName, String channelName, BlockBasic b)
          throws RemoteException {
    try (LocalLock _ = new LocalLock(lock.writeLock())) {
      return order.sendBlock(chainName, channelName, b);
    }
  }


  public void createChannel(String chainName, String channelName)
          throws RemoteException {
    try (LocalLock _ = new LocalLock(lock.writeLock())) {
      order.createChannel(chainName, channelName);
    }
  }


  private void syncAllChannels() {
    try {
      log.info("start synchronized all chain/channel");
      String[] chains = order.allChainNames();

      for (String chain : chains) {
        String[] channels = order.allChannelNames(chain);

        for (String channel : channels) {
          if (! channelExists(chain, channel)) {
            syncChannel(chain, channel);
          }
          syncBlock(chain, channel);
        }
      }
    } catch(Exception e) {
      log.error("synchronized all channels", e);
    }
  }


  /**
   * 同步: 拉取主节点的链到本地链
   */
  private void syncBlock(String chain, String channel) {
    try (LocalLock _ = new LocalLock(lock.writeLock())) {
      log.debug("synchronized block", chain, "/", channel);

      byte[] lkey = order.lastBlockKey(chain, channel);
      Deque<Block> syncBlockStack = new LinkedList<>();

      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chain);
      BlockFileSystem.InnerChannel ch = ca.openChannel(channel);
      ISigner si = getSigner(chain, channel);

      while (ch.search(lkey) == null) {
        Block b = order.search(chain, channel, lkey);
        if (b == null && !si.verify(b)) {
          log.error("synchronized block", JsonHelper.toJSON(b));
          break;
        }
        syncBlockStack.push(b);
        lkey = b.key;
      }

      if (! Arrays.equals(ch.lastBlockKey(), lkey)) {
        log.error("synchronized block, key not find in last block:",
                Hex.upperHex(lkey));
        return;
      }

      while (syncBlockStack.size() > 0) {
        Block b = syncBlockStack.pop();
        ch.pushOriginal(b);
      }
      ca.commit();
    } catch (Exception e) {
      log.error("synchronized block", e);
    }
  }


  private void syncChannel(String chain, String channel) {
    try (LocalLock _ = new LocalLock(lock.writeLock())) {
      log.debug("synchronized channel setting", chain, "/", channel);
      byte[] gkey = order.genesisKey(chain, channel);
      Block gb = order.search(chain, channel, gkey);

      ISigner si = getSigner(chain, channel);
      if (! si.verify(gb)) {
        log.error("synchronized genesis block verify fail",
                JsonHelper.toJSON(gb));
        return;
      }

      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chain);
      ca.createChannel(channel, getSigner(chain, channel), gb);
      ca.commit();
    } catch (RemoteException e) {
      log.error("synchronized channel", e);
    }
  }


  /**
   * 接受 Order 发送的同步消息并同步本地数据
   */
  public class ChainSyncListener extends GLHandle {

    private ChainSyncListener() {
      GlobalEventBus.me().on(Names.chain_sync, this);
      EventLoop.me().add(() -> syncAllChannels());
    }

    @Override
    public void objectChanged(NamingEvent e) {
      String chain   = (String) e.getChangeInfo();
      String channel = (String) e.getNewBinding().getObject();

      switch (e.getType()) {
        case NEW_CHANNEL:
          syncChannel(chain, channel);
          break;

        case NEW_BLOCK:
          syncBlock(chain, channel);
          break;

        default:
          log.error("bad chain synchronized type");
          break;
      }
    }
  }
}
