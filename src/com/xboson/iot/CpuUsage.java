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
