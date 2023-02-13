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
// 文件创建日期: 18-12-9 上午10:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/distributed/XLock.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.distributed;

import com.xboson.been.XBosonException;
import com.xboson.rpc.IXRemote;
import com.xboson.util.Tool;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 算法: 在 0 节点上执行本地锁, 其他节点使用 0 节点的锁服务
 */
public class XLock {

  private static final String NOT_RE_ENTRY = "Prohibit lock reentrance";
  private static final String RPC_NAME = "XB.rpc.LocalLockPool";
  private static XLock instance;

  private MasterMode<LocalLockPool> mm;


  /**
   * 必须在系统启动后立即注册锁服务, 保证其他节点随时可访问
   */
  public static XLock me() {
    if (instance == null) {
      synchronized (XLock.class) {
        if (instance == null) {
          instance = new XLock();
        }
      }
    }
    return instance;
  }


  private XLock() {
    try {
      mm = new MasterMode<>(RPC_NAME, ()-> new LocalLockPool());
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 获取集群上的锁
   */
  public ILockPool getLock() {
    return mm.get();
  }


  public interface ILockPool extends IXRemote {

    /**
     * 返回对应名称的锁对象, 线程安全
     * @param name 锁名称
     */
    ILock get(String name) throws RemoteException;
  }


  private class LocalLockPool implements ILockPool {
    private Map<String, ILock> pool;


    private LocalLockPool() {
      pool = new WeakHashMap<>();
    }


    @Override
    public synchronized ILock get(String name) throws RemoteException {
      if (Tool.isNulStr(name))
        throw new RemoteException("name is null");

      ILock lo = pool.get(name);
      if (lo == null) {
        lo = new LocalLock();
        pool.put(name, lo);
      }
      return lo;
    }
  }


  private class LocalLock implements ILock {
    private ReentrantLock lo;


    private LocalLock() {
      lo = new ReentrantLock();
    }


    @Override
    public void lock() throws RemoteException {
      if (lo.isHeldByCurrentThread()) {
        throw new RemoteException(NOT_RE_ENTRY);
      }
      lo.lock();
    }


    @Override
    public void lockInterruptibly() throws InterruptedException, RemoteException {
      if (lo.isHeldByCurrentThread()) {
        throw new RemoteException(NOT_RE_ENTRY);
      }
      lo.lockInterruptibly();
    }


    @Override
    public boolean tryLock() throws RemoteException {
      if (lo.isHeldByCurrentThread()) {
        throw new RemoteException(NOT_RE_ENTRY);
      }
      return lo.tryLock();
    }


    @Override
    public boolean tryLock(long ms)
            throws InterruptedException, RemoteException {
      if (lo.isHeldByCurrentThread()) {
        throw new RemoteException(NOT_RE_ENTRY);
      }
      return lo.tryLock(ms, TimeUnit.MILLISECONDS);
    }


    @Override
    public void unlock() throws RemoteException {
      lo.unlock();
    }
  }
}
