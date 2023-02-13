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
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.ReverseIterator;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.event.EventContext;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class GlobalEventContext extends InitialContext implements EventContext {

  private Set<GlobalListener> listeners;
  private String name;
  private Binding oldbind;
  private Log log;
  private boolean skip_error;
  private final long myselfid;
  private boolean isLiLo;

  private String channel_name;


  public GlobalEventContext(String name, long myselfid) throws NamingException {
    super(true);
    this.listeners = Collections.synchronizedSet(new LinkedHashSet<>());
    this.name      = name;
    this.log       = LogFactory.create("global-event."+ name);
    this.myselfid  = myselfid;
    this.isLiLo    = true;

    //
    // 带有 sys 开头的消息不会在集群中路由
    //
    if (name.indexOf("sys.") != 0) {
      this.channel_name = Names.CHANNEL_PREFIX + name;
    }

    if (name.equals(Names.inner_error)) {
      skip_error = true;
    }
  }


  /**
   * 创建实例并返回, 将创建的实例以 name 为键插入 map 中.
   */
  public static GlobalEventContext create(String name,
                                          Map<String, GlobalEventContext> map,
                                          long myselfid) {
    try {
      GlobalEventContext gec = new GlobalEventContext(name, myselfid);
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


  void destory() {
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
      try (IRedis client = RedisMesmerizer.me().open()) {
        EventPackage ep = new EventPackage(data, type, info, myselfid);
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

    Binding newbind = new Binding(name, data);
    NamingEvent event = new NamingEvent(
            this, type, newbind, oldbind, info);
    oldbind = newbind;

    Iterator<GlobalListener> its = isLiLo
            ? listeners.iterator() : new ReverseIterator(listeners);

    while (its.hasNext()) {
      EmitWithoutCluster.emit(its.next(), event, skip_error);
    }
  }


  /**
   * 设置接收消息的顺序.
   *    true: (默认) 先注册的监听器先接受到消息
   *    false: 后注册的监听器, 先接受到消息
   * @param isLilo
   */
  void setEmitOrder(boolean isLilo) {
    this.isLiLo = isLilo;
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
