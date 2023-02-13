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
// 文件创建日期: 17-11-12 上午9:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/OnExitHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.naming.event.NamingEvent;

/**
 * 监听系统退出的方便实现, 自动将自身注册到全局事件上.
 */
public abstract class OnExitHandle extends GLHandle {

  private static boolean orderInit = false;


  public OnExitHandle() {
    GlobalEventBus.me().on(Names.exit, this);

    if (!orderInit) {
      //
      // 只在第一个使用 OnExitHandle 对象的地方设置一次消息顺序.
      //
      orderInit = true;
      GlobalEventBus.me().setEmitOrder(Names.exit, false);
    }
  }


  public void objectChanged(NamingEvent namingEvent) {
    String name = namingEvent.getNewBinding().getName();
    Log log = getLog();

    switch (name) {
      case Names.exit:
        exit();
        log.info("destory on exit");
        return;
    }
  }


  /**
   * 从全局事件中, 移除自身.
   */
  public void removeExitListener() {
    boolean rm = GlobalEventBus.me().off(Names.exit, this);
    assert rm : "must removed";
  }


  /**
   * 系统退出时被调用
   */
  protected abstract void exit();
}
