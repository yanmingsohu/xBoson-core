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
// 文件创建日期: 17-11-27 上午7:27
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/timer/TimeFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event.timer;

import com.xboson.event.OnExitHandle;

import java.util.Timer;


/**
 * 维持全局唯一定时器
 */
public class TimeFactory extends OnExitHandle {

  private static TimeFactory instance;
  private Timer timer;


  private TimeFactory() {
    timer = new Timer("global-timer-despatch", true);
  }


  @Override
  protected void exit() {
    try {
      timer.cancel();
    } catch(Exception e) {
      getLog().error("Cancel", e);
    }

    try {
      timer.purge();
    } catch(Exception e) {
      getLog().error("Purge", e);
    }
  }


  public static Timer me() {
    if (instance == null) {
      synchronized (TimeFactory.class) {
        if (instance == null) {
          instance = new TimeFactory();
        }
      }
    }
    return instance.timer;
  }
}
