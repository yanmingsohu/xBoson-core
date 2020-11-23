////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/CpuUsage.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

/**
 * 快速计算 cpu 利用率
 */
public class CpuUsage {

  public final static int MAX_CHECK_TIME = 30;

  private final int total;
  private long be;
  private long ed;
  private int check;
  private long taskUsage;
  private long blankTime;
  private Object lock;


  /**
   * 在 count 个任务结束后停止检测, 减少因检测产生的开销
   */
  public CpuUsage(int count) {
    this.total = count << 1;
    this.lock = new Object();
  }


  /**
   * 在任务开始时调用该方法
   */
  public void begin() {
    if (check < total) {
      ++check;
      be = System.nanoTime();
      blankTime = be - ed;
      if (check == total-1) {
        unlock();
      }
    }
  }


  /**
   * 任务结束时调用
   */
  public void end() {
    if (check < total) {
      ++check;
      ed = System.nanoTime();
      taskUsage += ed - be;
      if (check == total-1) {
        unlock();
      }
    }
  }


  /**
   * 返回 cpu 占用率, 0~100
   */
  public int cpu() {
    if (taskUsage == 0) return 0;
    double t = blankTime + taskUsage;
    if (t == 0) return 0;
    return (int)((taskUsage / t) * 100);
  }


  /**
   * 等待测试结果, 通常在 100ms 内返回
   */
  public void waitTest() {
    try {
      synchronized (lock) {
        lock.wait(MAX_CHECK_TIME);
      }
    } catch (Exception e) {
    }
  }


  /**
   * 重启停止的检测, 重置数据
   */
  public void reset() {
    check = 0;
    taskUsage = 0;
    blankTime = 0;
    ed = be = System.nanoTime();
    unlock();
  }


  private void unlock() {
    try {
      synchronized (lock) {
        lock.notify();
      }
    } catch(Exception e) {}
  }
}
