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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/GLHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;

import com.xboson.log.ILogName;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.naming.event.NamingExceptionEvent;

/**
 * 默认实现
 */
public abstract class GLHandle implements GlobalListener, ILogName {
  private Log log = null;


  protected final synchronized Log getLog() {
    if (log == null) {
      log = LogFactory.create(this);
    }
    return log;
  }


  public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
    getLog().error("GLHandle: " + namingExceptionEvent);
  }
}
