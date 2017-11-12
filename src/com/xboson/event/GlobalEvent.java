////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月11日 上午07:16:48
// 原始文件路径: xBoson/src/com/xboson/util/GlobalEvent.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.naming.event.*;
import java.util.*;

/**
 * 全局事件, 线程安全的
 * !! 该对象将支持集群
 */
public class GlobalEvent {


  private static GlobalEvent instance;
  static {
    // 必须在 static 中初始化
    instance = new GlobalEvent();
  }
  public static GlobalEvent me() {
      return instance;
  }


  private Map<String, GlobalEventContext> contexts = new HashMap<>();
  private Log log  = LogFactory.create();


  private GlobalEvent() {
    log.info("Initialization Success");
  }


  private void destory() {
    Iterator<GlobalEventContext> it = contexts.values().iterator();
    while (it.hasNext()) {
      try {
        it.next().destory();
      } catch(Exception e) {
        log.error("Destory context", e);
      }
    }
    contexts = null;
    log.info("destoryed");
    log.info("---------- xBoson system shutdown ----------");
  }


  /**
   * 监听事件
   *
   * @param name 事件名称
   * @param listener 监听器对象
   */
  public synchronized void on(String name, GlobalListener listener) {
    if (name == null)
      throw new NullPointerException("name");
    if (listener == null)
      throw new NullPointerException("listener");

    try {
      GlobalEventContext context = contexts.get(name);
      if (context == null) {
        context = new GlobalEventContext(name);
        contexts.put(name, context);
      }
      context.on(listener);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * 删除事件监听器
   *
   * @param name 事件名称
   * @param listener 监听器对象, 如果 null 则删除所有在 name 上的监听器
   * @return 如果删除了监听器返回 true, 如果监听器不存在返回 false
   */
  public synchronized boolean off(String name, GlobalListener listener) {
    if (name != null) {
      GlobalEventContext context = contexts.get(name);
      if (context == null)
        return false;

      if (listener == null) {
        context.destory();
        contexts.remove(name);
        return true;
      } else {
        Set<GlobalListener> ls =context.getListeners();
        boolean ret = ls.remove(name);
        if (ls.isEmpty()) {
          context.destory();
          contexts.remove(name);
        }
        return ret;
      }
    }
    return false;
  }


  /**
   * 触发事件
   *
   * @param name 事件名称
   * @param data 数据
   * @param type 数据名称
   * @param info 扩展描述
   * @return
   */
  public synchronized boolean emit(
            String name, Object data, int type, String info) {

    GlobalEventContext context = contexts.get(name);
    if (context == null || context.getListeners().isEmpty())
      return false;

    context.emit(data, type, info);

    // 这是个特殊的事件, 当检查到退出系统的消息后, 等待所有处理器退出
    // 然后 GlobalEvent 执行自身的退出操作
    if (Names.exit.equals(name)) {
      destory();
    }

    return true;
  }


  /**
   * [ info = null ]
   * @see #emit(String, Object, int, String)
   */
  public boolean emit(String name, Object data, int type) {
    return emit(name, data, type, null);
  }


  /**
   * [ type = NamingEvent.OBJECT_CHANGED, info = null; ]
   * @see #emit(String, Object, int, String)
   */
  public boolean emit(String name, Object data) {
    return emit(name, data, NamingEvent.OBJECT_CHANGED, null);
  }


  /**
   * [ data = null, type = NamingEvent.OBJECT_CHANGED, info = null; ]
   * @see #emit(String, Object, int, String)
   */
  public boolean emit(String name) {
    return emit(name, null, NamingEvent.OBJECT_CHANGED, null);
  }

}
