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
// 文件创建日期: 17-11-27 上午7:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/timer/EarlyMorning.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event.timer;

import com.xboson.log.StaticLogProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;


/**
 * 每天凌晨都会执行, 准确时间是在明天 00:01:00 执行.
 */
public class EarlyMorning extends StaticLogProvider {

  private static final String logName = "early-morning";
  public static final Date first;
  public static final long hour24 = 24 * 60 * 60 * 1000;


  static {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, 1);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 1);
    c.set(Calendar.SECOND, 0);
    first = c.getTime();
  }


  private static void __add(final TimerTask task) {
    TimeFactory.me().scheduleAtFixedRate(task, first, hour24);
  }


  /**
   * 注册到凌晨事件中, 每天凌晨触发同步
   * @param task
   */
  public static void add(final TimerTask task) {
    __add(task);
    openLog(logName).info("DO", task, "Tomorrow");
  }


  /**
   * 注册到凌晨事件中, 每天凌晨触发同步
   * @param task
   */
  public static void add(final Runnable task) {
    __add(new RunnableWrap(task));
    openLog(logName).info("DO", task, "Tomorrow");
  }


}
