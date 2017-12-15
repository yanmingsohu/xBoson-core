////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-12 上午9:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/GlobalEventContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.RedisMesmerizer;
import redis.clients.jedis.Jedis;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.event.EventContext;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingListener;
import java.util.*;


class GlobalEventContext extends InitialContext implements EventContext {

  public final static String CHANNEL_PREFIX
          = "/" + GlobalEventContext.class.getName() + "/";

  private Set<GlobalListener> listeners;
  private String name;
  private Binding oldbind;
  private boolean skip_error;
  private Log log;

  private SubscribeThread sub_thread;
  private String channel_name;
  private long id;


  public GlobalEventContext(String name) throws NamingException {
    super(true);
    this.listeners = Collections.synchronizedSet(new LinkedHashSet<>());
    this.name = name;
    this.id = (long)(Long.MAX_VALUE / 2.0 * Math.random());
    this.log = LogFactory.create("Event::" + name);

    //
    // 带有 sys 开头的消息不会在集群中路由
    //
    if (name.indexOf("sys.") != 0) {
      this.channel_name = CHANNEL_PREFIX + name;
      sub_thread = new SubscribeThread(this);
      sub_thread.start();
    }

    if (name.equals(Names.inner_error)) {
      skip_error = true;
    }
  }


  /**
   * 创建实例并返回, 将创建的实例以 name 为键插入 map 中.
   */
  public static GlobalEventContext create(String name,
                                          Map<String, GlobalEventContext> map) {
    try {
      GlobalEventContext gec = new GlobalEventContext(name);
      map.put(name, gec);
      return gec;
    } catch (NamingException e) {
      throw new XBosonException(e);
    }
  }


  Set<GlobalListener> getListeners() {
    return listeners;
  }


  String getChannelName() {
    return channel_name;
  }


  long getSourceID() {
    return id;
  }


  void destory() {
    if (sub_thread != null) {
      sub_thread.destory();
      sub_thread = null;
    }
    listeners = null;
    oldbind = null;
    name = null;
  }


  void on(GlobalListener listener) {
    if (listener == null)
      throw new XBosonException.NullParamException("GlobalListener listener");

    listeners.add(listener);
  }


  /**
   * 自动判断消息是否需要发布到集群
   */
  void emit(Object data, int type, String info) {
    emitWithoutCluster(data, type, info);

    if (channel_name != null) {
      try (Jedis client = RedisMesmerizer.me().open()) {
        EventPackage ep = new EventPackage(data, type, info, id);
        client.publish(channel_name, ep.tojson());
      }
    }
  }


  /**
   * 只在系统内部发出消息
   */
  void emitWithoutCluster(Object data, int type, String info) {
    if (listeners.size() < 1)
      return;

    Iterator<GlobalListener> its = listeners.iterator();

    Binding newbind = new Binding(name, data);
    NamingEvent event = new NamingEvent(
            this, type, newbind, oldbind, info);
    oldbind = newbind;

    while (its.hasNext()) {
      try {
        its.next().objectChanged(event);
      } catch (Exception err) {
        if (skip_error) {
          log.warn("skip error by error");
        } else {
          QuickSender.emitError(err, this);
        }
      }
    }
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
}
