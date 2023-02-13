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
// 文件创建日期: 17-12-18 下午2:59
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/EmitWithoutCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.log.StaticLogProvider;

import javax.naming.event.NamingEvent;


/**
 * 仅在当前进程内发送消息
 */
public class EmitWithoutCluster extends StaticLogProvider {

  private EmitWithoutCluster() {
  }


  public static void emit(GlobalListener gl, NamingEvent data, boolean skip_error) {
    EventLoop.me().add(new PushEvent(gl, data, skip_error));
  }


  private static class PushEvent implements Runnable {
    private NamingEvent data;
    private GlobalListener who;
    private boolean skip_error;

    private PushEvent(GlobalListener who, NamingEvent data, boolean skip_error) {
      this.who = who;
      this.data = data;
      this.skip_error = skip_error;
    }

    @Override
    public void run() {
      try {
        who.objectChanged(data);
      } catch(Exception err) {
        if (skip_error) {
          openLog(EmitWithoutCluster.class).warn(
                  "Skip error by error", err);
        } else {
          QuickSender.emitError(err, this);
        }
      }
    }


    @Override
    public String toString() {
      return "[ PushEvent: "+ data.getNewBinding() + ", TO: "+ who +" ]";
    }
  }

}
