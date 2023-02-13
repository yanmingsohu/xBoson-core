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
// 文件创建日期: 18-12-9 下午2:02
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/LockImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.ApiPath;
import com.xboson.app.AppContext;
import com.xboson.been.XBosonException;
import com.xboson.distributed.ILock;
import com.xboson.distributed.XLock;
import com.xboson.rpc.ClusterManager;
import com.xboson.util.Tool;

import java.rmi.RemoteException;


/**
 * 集群锁
 */
public class LockImpl {

  private String nodeId;
  private XLock xl;
  private AppContext app;


  public LockImpl() {
    this.xl     = XLock.me();
    this.app    = AppContext.me();
    this.nodeId = ClusterManager.me().localNodeID();
  }


  public Warp open(String name) throws RemoteException {
    if (Tool.isNulStr(name))
      throw new XBosonException.BadParameter("name", "is null");

    ILock local = xl.getLock().get(name);
    Warp w = new Warp(name, local);
    ModuleHandleContext.autoClose(w);
    return w;
  }


  public Warp api() throws Exception {
    return open("_$API-LOCK:"+ app.getCurrentApiPath());
  }


  public Warp mod() throws Exception {
    return open("_$MODEL-LOCK:"+ ApiPath.getModPath(app.getExtendParameter()));
  }


  public Warp org() throws Exception {
    return open("_$ORG-LOCK:"+ app.originalOrg());
  }


  public Warp app() throws Exception {
    return open("_$APP-LOCK:"+ ApiPath.getAppPath(app.getExtendParameter()));
  }


  public Warp node() throws Exception {
    return open("_$NODE-LOCK:"+ nodeId);
  }


  public class Warp implements ILock, AutoCloseable {
    public final String name;
    private final ILock l;


    private Warp(String name, ILock local) {
      this.name = name;
      this.l = local;
    }


    @Override
    public void lock() throws RemoteException {
      l.lock();
    }


    @Override
    public void lockInterruptibly() throws InterruptedException, RemoteException {
      l.lockInterruptibly();
    }


    @Override
    public boolean tryLock() throws RemoteException {
      return l.tryLock();
    }


    @Override
    public boolean tryLock(long ms) throws InterruptedException, RemoteException {
      return l.tryLock(ms);
    }


    @Override
    public void unlock() throws RemoteException {
      l.unlock();
    }


    @Override
    public void close() throws Exception {
      l.unlock();
    }
  }
}
