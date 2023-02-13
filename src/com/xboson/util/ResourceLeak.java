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
// 文件创建日期: 18-1-8 上午11:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ResourceLeak.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.event.timer.TimeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.util.TimerTask;


/**
 * 用于检查资源泄漏
 */
public class ResourceLeak extends TimerTask implements AutoCloseable {

  private Exception stack;
  private Log log;


  public ResourceLeak(Object who) {
    log = LogFactory.create("Call stack");
    stack = new Exception("Resource Leak " + who);
    TimeFactory.me().schedule(this, 10000, 10000);
  }


  @Override
  public void run() {
    log.warn("Resource leak", Tool.allStack(stack));
  }


  @Override
  public void close() throws Exception {
    super.cancel();
  }
}
