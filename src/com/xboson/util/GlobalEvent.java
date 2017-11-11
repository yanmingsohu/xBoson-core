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

package com.xboson.util;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import jdk.nashorn.internal.objects.Global;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.event.*;
import java.util.*;

/**
 * 全局事件, 线程安全的
 * !! 该对象将支持集群
 */
public class GlobalEvent {

  /**
   * 已有的事件列表
   */
  public final class Names {
    /** exit (null) 容器销毁前发出 */
    public static final String exit = "sys.exit";
    public static final String config = "sys.config_success";
    public static final String initialization = "sys.initialization";
  }


  private static GlobalEvent instance;
  static {
    instance = new GlobalEvent();
  }
  public static GlobalEvent me() {
      return instance;
  }


  /**
   * 事件监听器必须实现该接口
   */
  public interface GlobalListener extends ObjectChangeListener {
  }


  /**
   * 监听系统退出的方便实现
   */
  static public abstract class OnExit extends GLHandle {
    public OnExit() {
      me().on(Names.exit, this);
    }

    public void objectChanged(NamingEvent namingEvent) {
      String name = namingEvent.getNewBinding().getName();
      Log log = LogFactory.create(getClass());

      switch (name) {
        case Names.exit:
          exit();
          log.info("destory on exit");
          return;
      }
    }

    /**
     * 系统退出时被调用
     */
    protected abstract void exit();
  }


  /**
   * 默认实现
   */
  static public abstract class GLHandle implements GlobalListener {
    public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
      System.out.println("GLHandle: " + namingExceptionEvent);
    }
  }


  class GlobalEventContext extends InitialContext implements EventContext {
    private Set<GlobalListener> listeners;
    private String name;
    private Binding oldbind;

    public GlobalEventContext(String name) throws NamingException {
      super(true);
      this.listeners = new HashSet<>();
      this.name = name;
    }

    @Override
    public void addNamingListener(Name name, int i, NamingListener namingListener)
            throws NamingException {
      throw new NamingException("not support");
    }

    @Override
    public void addNamingListener(String s, int i, NamingListener namingListener)
            throws NamingException {
      throw new NamingException("not support");
    }

    @Override
    public void removeNamingListener(NamingListener namingListener)
            throws NamingException {
      throw new NamingException("not support");
    }

    @Override
    public boolean targetMustExist() throws NamingException {
      return false;
    }


    void destory() {
      listeners = null;
      oldbind = null;
      name = null;
    }


    void on(GlobalListener listener) {
      listeners.add(listener);
      // log.debug("ON", name, listener);
    }


    void emit(Object data, int type, String info) {
      Iterator<GlobalListener> its = listeners.iterator();

      Binding newbind = new Binding(name, data);

      NamingEvent event = new NamingEvent(
              this, type /*NamingEvent.OBJECT_CHANGED*/,
              newbind, oldbind, info);

      while (its.hasNext()) {
        its.next().objectChanged(event);
      }

      oldbind = newbind;
      // log.debug("EMIT", name, data, info);
    }
  }

///////////////////////////////////////////////////////////////////////////////
// 实例对象
///////////////////////////////////////////////////////////////////////////////

  private Map<String, GlobalEventContext> contexts = new HashMap<>();
  private Log log  = LogFactory.create();


  private GlobalEvent() {
    log.info("Initialization Success");
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
        boolean ret = context.listeners.remove(name);
        if (context.listeners.isEmpty()) {
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
  public synchronized boolean emit(String name,
                                   Object data,
                                   int type,
                                   String info) {
    GlobalEventContext context = contexts.get(name);
    if (context == null || context.listeners.isEmpty())
      return false;

    context.emit(data, type, info);
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
