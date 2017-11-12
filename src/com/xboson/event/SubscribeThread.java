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

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
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
  private Log log;
  private boolean running;


  public SubscribeThread(GlobalEventContext context) {
    this.channel_name = context.getChannelName();
    this.mySelfid = context.getSourceID();
    this.context = context;
    this.log = LogFactory.create("SubscribeThread::" + channel_name);
    this.running = true;
  }


  public void start() {
    if (thread != null) {
      throw new RuntimeException("donot start again");
    }
    running = true;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }


  public void destory() {
    running = false;
    unsubscribe();
    Tool.waitOver(thread);
    thread = null;
    client = null;
    log.debug("destoryed");
  }


  public void onMessage(String channel, String message) {
    try {
      EventPackage ep = EventPackage.fromjson(message);
      if (ep.from != mySelfid) {
        ep.parseData();
        context.emitWithoutCluster(ep.data, ep.type, ep.info);
      }
    } catch (Exception e) {
      log.error("onMessage()", e);
    }
  }


  @Override
  public void run() {
    while (running) {
      try {
        client = RedisMesmerizer.me().open();
        client.subscribe(this, channel_name);

      } catch (Exception e) {
        log.debug("STOP", e.getMessage());
        Tool.sleep(1000);

      } finally {
        // 必须在这里停止 client
        Tool.close(client);
        client = null;
        running = false;
      }
    }
  }
}
