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
