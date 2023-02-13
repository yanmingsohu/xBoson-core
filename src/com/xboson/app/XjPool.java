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
// 文件创建日期: 17-11-22 下午2:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/XjPool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.XBosonException;
import com.xboson.log.ILogName;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 方便创建层级式的对象池
 */
public abstract class XjPool<E> implements ILogName {

  private Map<String, E> pool;
  protected Log log;


  protected XjPool() {
    pool = new ConcurrentHashMap<>();
    log = LogFactory.create(this);
  }


  /**
   * 创建对象, 如果已经存在则返回
   * @param name
   * @return
   */
  protected E getWithCreate(String name) {
    if (name == null) {
      throw new XBosonException.NullParamException("String name");
    }
    E ret = pool.get(name);

    if (ret == null) {
      //
      // 即使 pool 已经同步, 仍然需要同步块来保证 createItem() 只调用一次.
      //
      synchronized (this) {
        ret = pool.get(name);
        if (ret == null) {
          ret = createItem(name);
          pool.put(name, ret);
        }
      }
    }
    return ret;
  }


  /**
   * 实现该方法, 当需要创建一个新的类型实例时被调用.
   * 该方法实现中通常不需要额外的同步操作, 是线程安全的.
   */
  protected abstract E createItem(String name);

}
