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
// 文件创建日期: 17-11-27 上午9:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/timer/RunnableWrap.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event.timer;

import java.util.TimerTask;


/**
 * Runnable 接口包裹成为 TimerTask
 *
 * @see java.lang.Runnable
 */
public class RunnableWrap extends TimerTask {

  private Runnable run;


  public RunnableWrap(Runnable run) {
    this.run = run;
  }


  public void run() {
    run.run();
  }


  public static TimerTask wrap(Runnable r) {
    return new RunnableWrap(r);
  }
}
