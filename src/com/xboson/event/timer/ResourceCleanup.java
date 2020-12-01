////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
