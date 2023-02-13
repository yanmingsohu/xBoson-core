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
// 文件创建日期: 17-11-20 上午10:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/EventQueueMigrationThread.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.fs.redis.IFileSystemConfig;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;

import javax.naming.event.NamingEvent;
import java.util.List;


/**
 * 事件队列迁移线程, 该线程将队列中的事件取出后发布到集群订阅通知中.
 * 允许有多个该线程的实例来分担任务, 但每个一条消息只被一个线程处理一次.
 * 在系统进入销毁程序, 该线程自动终止.
 */
public class EventQueueMigrationThread extends OnExitHandle implements Runnable {

  public static final int QUEUE_TIMEOUT   = 5; // 秒
  public static final int EVENT_TYPE = NamingEvent.OBJECT_CHANGED;

  private boolean running;
  private Thread myself;
  private Log log;
  private String queue_name;
  private String event_name;


  /**
   * 创建线程并启动
   */
  public EventQueueMigrationThread(IFileSystemConfig config) {
    this.event_name = config.configFileChangeEventName();
    this.queue_name = config.configQueueName();
    this.running = true;
    this.log = LogFactory.create(EventQueueMigrationThread.class, queue_name);

    myself = new Thread(this);
    myself.start();
    log.info(IConstant.INITIALIZATION);
  }


  public void run() {
    myself = Thread.currentThread();
    log.info("Start", myself);
    GlobalEventBus ge = GlobalEventBus.me();

    try (IRedis client = RedisMesmerizer.me().open()) {
      while (running) {
        //
        // 在队列上等待 QUEUE_TIMEOUT 秒后返回
        //
        List<String> ret = client.brpop(QUEUE_TIMEOUT, queue_name);
        if (ret.size() == 2) {
          ge.emit(event_name, ret.get(1), EVENT_TYPE, ret.get(0));
        }
      }
    } finally {
      running = false;
    }
    log.info("Stop", myself);
  }


  /**
   * 该方法会尝试让线程终止
   */
  public void stop() {
    exit();
    removeExitListener();
  }


  public boolean isRunning() {
    return running;
  }


  @Override
  protected void exit() {
    if (!running) return;
    running = false;
    log.debug("Wait...", QUEUE_TIMEOUT +"s");
    Tool.waitOver(myself);
  }
}
