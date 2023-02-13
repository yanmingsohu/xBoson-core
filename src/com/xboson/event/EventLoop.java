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
// 文件创建日期: 17-12-19 下午12:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/EventLoop.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.ThreadMonitor;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.Tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * 一个全局单线程任务管理器, 所有任务都在一个线程中运行,
 * 适合各种初始化操作, 非紧急的任务.
 */
public class EventLoop implements ThreadFactory, IConstant {

  private final static String THREAD_NAME = "EventLoopThread";
  private final static int WARN_COUNT = 50;

  private static EventLoop instance;
  private ExecutorService worker;
  private Log log;
  private int inQueue;
  private int printWarn;
  private ThreadMonitor taskMonitor;


  private EventLoop() {
    this.worker = Executors.newSingleThreadExecutor(this);
    this.log = LogFactory.create("event-loop");
    this.printWarn = WARN_COUNT;
    this.taskMonitor = new ThreadMonitor();
    log.info(INITIALIZATION);
  }


  public static EventLoop me() {
    if (instance == null) {
      synchronized (EventLoop.class) {
        if (instance == null) {
          instance = new EventLoop();
        }
      }
    }
    return instance;
  }


  /**
   * 在线程上添加一个任务, 如果任务队列为空可以立即执行,
   * 否则等到队列中之前的任务执行完成, 任务一旦开始执行必须等到自行退出,
   * 下一个任务才能开始.
   */
  public void add(Runnable task) {
    add(task, -1);
  }


  /**
   * 在线程上添加一个任务, 如果任务队列为空可以立即执行,
   * 否则等到队列中之前的任务执行完成. 如果运行时间超过 maxRuntime(毫秒) 则停止任务.
   */
  public void add(Runnable task, long maxRuntime) {
    log.debug("Add task:", task, maxRuntime > 0 ? maxRuntime : "");
    worker.execute(new Wrap(task, maxRuntime));
    ++inQueue;

    if (inQueue > printWarn) {
      printWarn += WARN_COUNT;
      log.warn("Queued tasks", inQueue);
    }
  }


  /**
   * 必须在 GlobalEventBus.destory() 中调用
   * @see GlobalEventBus
   */
  void destory() {
    Tool.shutdown(worker);
    worker = null;
    log.info(DESTORYED);
  }


  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r, THREAD_NAME);
    t.setPriority(Thread.MIN_PRIORITY);
    log.info("Create Local Thread Object:", t);
    return t;
  }


  private class Wrap implements Runnable {
    private Runnable r;
    private long maxRuntime;

    private Wrap(Runnable r, long maxRuntime) {
      this.r = r;
      this.maxRuntime = maxRuntime;
    }

    public void run() {
      try {
        if (maxRuntime > 0) {
          taskMonitor.look(Thread.currentThread(), maxRuntime);
        }
        r.run();
      }
      catch (XBosonException.Shutdown e) {
        log.warn("Shutdown Task ["+ r +"],", e);
      }
      catch (ThreadDeath d) {
        log.error("Kill Task ["+ r +"],", d);
      }
      catch (Throwable t) {
        log.error("Fail Task ["+ r +"],", Tool.allStack(t));
      }
      finally {
        --inQueue;
        if (printWarn > WARN_COUNT) --printWarn;
        taskMonitor.purge();
      }
    }
  }

}
