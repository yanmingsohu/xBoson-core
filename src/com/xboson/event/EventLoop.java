////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
  private TaskMonitor taskMonitor;


  private EventLoop() {
    this.worker = Executors.newSingleThreadExecutor(this);
    this.log = LogFactory.create("event-loop");
    this.printWarn = WARN_COUNT;
    this.taskMonitor = new TaskMonitor();
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
   * 否则等到队列中之前的任务执行完成.
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


  /**
   * 每时刻监视一个线程, 当线程超时, 终止被监视线程并进入休眠.
   * 可以在多个监视线程间切换
   */
  private class TaskMonitor extends Thread {
    private final static int CHECK_WAIT = 1000;
    private Thread who;
    private long delay;
    private Log log;

    private TaskMonitor() {
      this.delay = -1;
      this.log = LogFactory.create("Task-Monitor");
      this.setDaemon(true);
      this.setName("Task-Monitor");
      this.start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
      for (;;) {
        try {
          synchronized (this) {
            if (delay <= 0) {
              this.wait();
              continue;
            }

            this.wait(delay);

            if (who != null && who.getState() != State.TERMINATED) {
              log.warn("Task timeout and interrupt:",
                      who.getName(), who.getState());
              who.stop();
            }
            clear();
          }
        } catch (InterruptedException e) {
          // nothing.
        }
      }
    }

    public void look(Thread who, long delay) {
      synchronized (this) {
        this.who = who;
        this.delay = delay;
        this.interrupt();
      }
    }

    public void purge() {
      synchronized (this) {
        clear();
        this.interrupt();
      }
    }

    private void clear() {
      this.who = null;
      this.delay = -1;
    }
  }
}
