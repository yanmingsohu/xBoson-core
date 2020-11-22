////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-21 下午3:09
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/SystemNow.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.event.timer.TimeFactory;

import java.util.TimerTask;


/**
 * 一个不精准的系统时间, 用于大并发
 * 替代 System.currentTimeMillis()
 */
public class SystemNow implements AutoCloseable {

  /**
   * 可直接访问该字段, 当前系统时间
   */
  public long now;
  private TimerTask task;


  /**
   * 1 秒精度系统时间
   */
  public SystemNow() {
    this(1000);
  }


  /**
   * 指定精度的系统时间
   * @param precision 精度, 单位毫秒, 不能小于 10 毫秒
   */
  public SystemNow(long precision) {
    if (precision < 10) {
      throw new XBosonException.BadParameter("precision",
              "Not less than 10 milliseconds");
    }
    update();

    task = new TimerTask() {
      public void run() {
        update();
      }
    };
    TimeFactory.me().schedule(task, precision, precision);
  }


  private void update() {
    now = System.currentTimeMillis();
  }


  @Override
  public void close() throws Exception {
    task.cancel();
  }


  @Override
  protected void finalize() throws Throwable {
    close();
  }
}
