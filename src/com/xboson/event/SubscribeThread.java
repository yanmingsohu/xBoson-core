////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-12 上午10:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/SubscribeThread.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;


class SubscribeThread extends JedisPubSub implements Runnable {

  private String channel_name;
  private GlobalEventContext context;
  private Thread thread;
  private long mySelfid;
  private Jedis client;


  public SubscribeThread(GlobalEventContext context) {
    this.channel_name = context.getChannelName();
    this.mySelfid = context.getSourceID();
    this.context = context;
  }


  public void start() {
    if (thread != null) {
      throw new RuntimeException("donot start again");
    }
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }


  public void destory() {
    if (client != null) {
      try {
        client.quit();
      } catch(Exception e) {}
      client = null;
    }
    if (thread != null) {
      try {
        thread.join();
      } catch (InterruptedException e) {}
      thread = null;
    }
  }


  public void onMessage(String channel, String message) {
    try {
      EventPackage ep = EventPackage.fromjson(message);
      if (ep.from != mySelfid) {
        context.emitWithoutCluster(ep.data, ep.type, ep.info);
      }
    } catch (Exception e) {
      QuickSender.emitError(e, this);
    }
  }


  @Override
  public void run() {
    try {
      client = RedisMesmerizer.me().open();
      client.subscribe(this, channel_name);

    } catch(Exception e) {
      // client 在关闭后, 或 pool 被关闭, 会抛出一个异常, 没什么关系
      // System.err.println("SubscribeThread STOP " + e.getMessage());

    } finally {
      Tool.close(client);
    }
  }
}
