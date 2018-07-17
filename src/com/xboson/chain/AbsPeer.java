////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-7-17 上午8:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/AbsPeer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import com.xboson.been.XBosonException;
import com.xboson.util.LocalLock;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public abstract class AbsPeer implements ISignerProvider, IPeer {

  private ISignerProvider sp;
  /** 对于读写操作的锁对象 */
  protected final ReadWriteLock lock;


  protected AbsPeer() {
    this.sp   = new DefaultSignerProvider();
    this.lock = new ReentrantReadWriteLock(false);
  }


  @Override
  public Block search(String chainName, String channelName, byte[] key) {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chainName);
      return ca.openChannel(channelName).search(key);
    }
  }


  @Override
  public byte[] worldState(String chainName, String channelName) {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chainName);
      return ca.openChannel(channelName).worldState();
    }
  }


  @Override
  public byte[] lastBlockKey(String chainName, String channelName) {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chainName);
      return ca.openChannel(channelName).lastBlockKey();
    }
  }


  @Override
  public byte[] genesisKey(String chain, String channel) throws RemoteException {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chain);
      return ca.openChannel(channel).genesisKey();
    }
  }


  @Override
  public String[] allChainNames() {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      Set<String> names = BlockFileSystem.me().allChainNames();
      return names.toArray(new String[names.size()]);
    }
  }


  @Override
  public String[] allChannelNames(String chain) {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chain);
      Set<String> names = ca.allChannelNames();
      return names.toArray(new String[names.size()]);
    }
  }


  @Override
  public boolean channelExists(String chain, String channel) {
    try (LocalLock _ = new LocalLock(lock.readLock())) {
      BlockFileSystem bfs = BlockFileSystem.me();
      if (bfs.chainExists(chain)) {
        BlockFileSystem.InnerChain ca = bfs.getChain(chain);
        return ca.channelExists(channel);
      }
      return false;
    }
  }


  protected byte[] sendBlockLocal(String chain, String channel, BlockBasic b) {
    BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chain);
    synchronized (ca) {
      try {
        BlockFileSystem.InnerChannel ch = ca.openChannel(channel);
        return ch.push(b);
      } finally {
        ca.commit();
      }
    }
  }


  protected void createChannelLocal(String chainName, String channelName) {
    try (LocalLock _ = new LocalLock(lock.writeLock())) {
      BlockFileSystem.InnerChain ca = BlockFileSystem.me().getChain(chainName);
      ca.createChannel(channelName, getSigner(chainName, channelName));
      ca.commit();
    }
  }


  /**
   * 返回通道的签名对象
   */
  public ISigner getSigner(String chainName, String channelName) {
    return sp.getSigner(chainName, channelName);
  }


  /**
   * 注册一个签名器
   */
  public void registerSigner(ISignerProvider sp) {
    if (sp == null) throw new XBosonException.BadParameter(
        "ISignerProvider sp", "is null");
    this.sp = sp;
  }


  public static class DefaultSignerProvider implements ISignerProvider {
    @Override
    public ISigner getSigner(String chainName, String channelName) {
      return new NoneSigner();
    }
  }


  /**
   * 不执行签名, 总是验证成功
   */
  public static class NoneSigner implements ISigner {
    @Override
    public void sign(Block block) {
    }

    @Override
    public boolean verify(Block block) {
      return true;
    }
  }

}
