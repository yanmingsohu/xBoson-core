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
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Tool;
import redis.clients.jedis.JedisPubSub;


/**
 * 在独立的线程中取 redis 消息队列, 并压入另一个线程中去执行.
 */
class SubscribeThread extends JedisPubSub implements Runnable {

  public final static String SUBSCRIBE_PATTERN = Names.CHANNEL_PREFIX + "*";

  private GlobalEventBus bus;
  private Thread thread;
  private Log log;
  private boolean running;
  private final long mySelfid;


  public SubscribeThread(GlobalEventBus bus, long mySelfid) {
    this.mySelfid = mySelfid;
    this.log      = LogFactory.create();
    this.running  = true;
    this.bus      = bus;
  }


  public void start() {
    if (thread != null) {
      throw new RuntimeException("Don't start again");
    }
    running = true;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }


  /**
   * 必须且只能在 GlobalEventBus.destory() 中调用
   * @see GlobalEventBus
   */
  void destory() {
    running = false;
    punsubscribe();
    Tool.waitOver(thread);
    thread = null;
    log.info("destoryed");
  }


  @Override
  public void onPMessage(String pattern, String channel, String message) {
    try {
      EventPackage ep = EventPackage.fromjson(message);
      if (ep.from != mySelfid) {
        ep.parseData();
        GlobalEventContext context = bus.getContext(false,
                channel.substring(Names.CHANNEL_PREFIX.length()));

        if (context != null) {
          context.emitWithoutCluster(ep.data, ep.type, ep.info);
        }
      }
    } catch (Exception e) {
      log.error("onMessage()", e);
    }
  }


  @Override
  public void run() {
    while (running) {
      try (IRedis client = RedisMesmerizer.me().open()) {
        client.psubscribe(this, SUBSCRIBE_PATTERN);

      } catch (Exception e) {
        log.debug("STOP", e.getMessage());
        Tool.sleep(1000);

      } finally {
        running = false;
      }
    }
  }
}
