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
// 文件创建日期: 18-12-9 下午12:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/distributed/ILock.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.distributed;

import com.xboson.rpc.IXRemote;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;


/**
 * @see java.util.concurrent.locks.Lock
 */
public interface ILock extends IXRemote {

  /**
   * @see Lock#lock()
   */
  void lock() throws RemoteException;


  /**
   * @see Lock#lockInterruptibly()
   */
  void lockInterruptibly() throws InterruptedException, RemoteException;


  /**
   * @see Lock#tryLock()
   */
  boolean tryLock() throws RemoteException;


  /**
   * 超时单位毫秒
   * @see Lock#tryLock()
   */
  boolean tryLock(long ms)
          throws InterruptedException, RemoteException;


  /**
   * @see Lock#unlock()
   */
  void unlock() throws RemoteException;
}
