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
// 文件创建日期: 18-8-14 上午10:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/WeakMemCache.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * 使用 WeakHashMap 实现的内存对象缓存, 多线程安全
 */
public class WeakMemCache<K, V> {

  public interface ICreator<K, V> {
    V create(K init);
  }


  private Map<K, V> pool;
  private ICreator<K, V> creator;


  public WeakMemCache(ICreator<K, V> c) {
    this.pool = new WeakHashMap<>();
    this.creator = c;
  }


  public V getOrCreate(K key) {
    if (key == null)
      throw new XBosonException.NullParamException("key");

    V value;
    synchronized (this) {
      value = pool.get(key);

      if (value == null) {
        value = creator.create(key);
        pool.put(key, value);
      }
    }
    return value;
  }


  public void remove(K key) {
    synchronized (this) {
      pool.remove(key);
    }
  }
}
