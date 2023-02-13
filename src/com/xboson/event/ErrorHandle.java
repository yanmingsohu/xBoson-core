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
// 文件创建日期: 17-11-12 上午11:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/ErrorHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import javax.naming.Binding;
import javax.naming.event.NamingEvent;

/**
 * 注册错误消息监听器
 */
public abstract class ErrorHandle extends GLHandle implements GlobalListener {

  /**
   * 默认构造器注册到全局
   */
  public ErrorHandle() {
    GlobalEventBus.me().on(Names.inner_error, this);
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    try {
      Binding b = namingEvent.getNewBinding();
      if (b.getName().equals(Names.inner_error)) {
        onError((Exception) b.getObject(), namingEvent.getChangeInfo().toString());
      } else {
        getLog().warn("ErrorHandle recive Non-error message", b);
      }
    } catch(Exception e) {
      getLog().error(e);
    }
  }


  /**
   * 当收到错误时被调用
   */
  abstract void onError(Exception err, String source);

}
