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
// 文件创建日期: 20-12-1 上午9:03
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/timer/ResourceCleanup.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event.timer;

import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.util.*;


/**
 * 定时资源回收器
 */
public class ResourceCleanup<K extends ResourceCleanup.IKeyTime> extends OnExitHandle {

  public static final long Interval = 1* 60*60*1000;

  /**
   * 当资源清除器执行清除方法时在该对象上加锁, 外部必须在访问资源时加锁
   */
  public final Object lock = new Object();
  private final ITypeCleanup tc;
  private final Log log = LogFactory.create("resource-cleanup");
  private String name;


  public <V extends AutoCloseable> ResourceCleanup(String name, Map<K, V> map) {
    this.tc = new MapCleanup(map);
    init(name);
  }


  private void init(String name) {
    this.name = name;
    TimeFactory.me().schedule(tc, Interval, Interval);
    log.debug(name, "created");
  }


  @Override
  protected final void exit() {
    tc.cancel();
  }


  private abstract class ITypeCleanup extends TimerTask {
    abstract void cleaning();

    public final void run() {
      log.debug(name, "is time to clean");
      synchronized (lock) {
        cleaning();
      }
    }
  }


  public interface IKeyTime {

    /**
     * 返回对象的冻结时间, 当超过指定时间后, 对象应该被回收
     */
    long freezingTime();


    /**
     * 通常在 equals 方法中更新时间
     */
    boolean equals(Object o);


    /**
     * 释放该 key 指定的资源时被调用, 调用在 lock 锁中,
     * @param v 当前 key 对应的值, 此时 k,v 都已经从 map 中删除
     */
    void release(Object v);
  }


  private class MapCleanup<V> extends ITypeCleanup {

    private Map<K, V> map;


    private  MapCleanup(Map<K, V> map) {
      this.map = map;
    }


    @Override
    public void cleaning() {
      List<IKeyTime> rm = new ArrayList<>(map.keySet().size());
      for (IKeyTime k : map.keySet()) {
        if (k.freezingTime() > Interval) {
          rm.add(k);
        }
      }

      for (IKeyTime k : rm) {
        Object v = map.remove(k);
        if (v != null) {
          try {
            k.release(v);
          } catch(Exception e) {
            log.debug(name, "clean got error", e);
            continue;
          }
        }
        log.debug(name, "clean", k);
      }
    }
  }
}
