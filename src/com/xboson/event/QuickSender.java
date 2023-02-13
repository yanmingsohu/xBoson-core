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
// 文件创建日期: 17-11-12 上午11:50
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/QuickSender.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;


import javax.naming.event.NamingEvent;

/**
 * 方便的发出错误消息的工具类,
 * 既可以使用 GlobalEventBus 发出消息, 也可以调用便捷方法.
 */
public final class QuickSender {

  private QuickSender() {}


  /**
   * 发出一个系统错误
   */
  public static void emitError(Exception e, Object source) {
    GlobalEventBus.me().emit(Names.inner_error, e,
            NamingEvent.OBJECT_ADDED, source.getClass().getName());
  }

}
