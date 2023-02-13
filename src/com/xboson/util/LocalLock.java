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
// 文件创建日期: 18-7-17 上午11:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/LocalLock.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.util.concurrent.locks.Lock;


/**
 * 在局部范围锁住线程, 将 try { lock() } finally { unlock() }
 * 转换为 try (lock()) {}
 */
public class LocalLock implements AutoCloseable {

  private Lock lock;


  /**
   * 构造并加锁
   */
  public LocalLock(Lock lock) {
    this.lock = lock;
    lock.lock();
  }


  @Override
  public void close() {
    if (lock != null) {
      lock.unlock();
      lock = null;
    }
  }
}
