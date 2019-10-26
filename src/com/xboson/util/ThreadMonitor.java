////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-10-26 下午9:54
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ThreadMonitor.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;


/**
 * 每时刻监视一个线程, 当线程超时, 终止被监视线程并进入休眠.
 * 可以在多个监视线程间切换, 复用该对象效率更高.
 * 线程安全, 本身将启动一个守护线程.
 */
public class ThreadMonitor {

  private final static String NAME = "Thread-Monitor";
  private final Object lock = new Object();

  private Thread who;
  private long delay;
  private Log log;
  private IStopper stopper;
  private Monitor monitor;


  public ThreadMonitor() {
    this(NAME);
  }


  public ThreadMonitor(String name) {
    if (null == name) throw new NullPointerException();
    this.delay = -1;
    this.log = LogFactory.create(name);
    this.stopper = new IStopper() {};
    this.monitor = new Monitor(name);
  }


  /**
   * 设置关闭线程的方法, 默认调用 Thread.stop()
   */
  public void setStopMethod(IStopper s) {
    if (s == null) throw new NullPointerException();
    this.stopper = s;
  }


  /**
   * 开始监视一个线程, 当线程超过 delay 毫秒后线程将被杀死, 如果之前有一个
   * 监视线程, 在调用后将切换到新线程上监视.
   * @param who 监视线程
   * @param delay 毫秒
   */
  public void look(Thread who, long delay) {
    synchronized (lock) {
      this.who = who;
      this.delay = delay;
      monitor.interrupt();
    }
  }


  /**
   * 如果监视线程在超时前完成, 调用该方法通知监视器停止监视行为.
   */
  public void purge() {
    synchronized (lock) {
      clear();
      monitor.interrupt();
    }
  }


  private void clear() {
    this.who = null;
    this.delay = -1;
  }


  private class Monitor extends Thread {

    private Monitor(String name) {
      this.setDaemon(true);
      this.setName(name);
      this.start();
    }

    @Override
    public void run() {
      for (;;) {
        try {
          synchronized (lock) {
            if (delay <= 0) {
              lock.wait();
              continue;
            }

            lock.wait(delay);

            if (who != null && who.getState() != State.TERMINATED) {
              log.warn("Task timeout and interrupt:",
                      who.getName(), who.getState());
              stopper.stop(who);
            }
            clear();
          }
        } catch (InterruptedException e) {
          // nothing.
        }
      }
    }
  }


  /**
   * 关闭线程的接口
   */
  public interface IStopper {

    /**
     * 关闭线程的方法, 默认调用 Thread.stop()
     */
    @SuppressWarnings("unchecked")
    default void stop(Thread thread) {
      thread.stop();
    }
  }

}
