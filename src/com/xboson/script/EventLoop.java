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
// 文件创建日期: 18-6-12 上午7:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/EventLoop.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


/**
 * 模拟 nodejs 消息队列.
 * 该对象线程安全.
 *
 *  [ 队列0, 队列1, 队列2, ...... 队列N ]
 *     |     |       |            |
 *    top.next  => task         last.next => null
 */
public class EventLoop implements IVisitByScript {

  private final ScriptObjectMirror process;
  private final Log log;
  private Task top;
  private Task last;


  public EventLoop(ScriptObjectMirror process) {
    this.process = process;
    this.log = LogFactory.create("js:EventLoop");
    this.top = new Task(null);
    this.last = top;

    if (process == null) {
      log.warn("Not set 'process' object");
    }
  }


  /**
   * 将任务函数压入队列
   */
  public void push(ScriptObjectMirror func) {
    if (func == null || (! func.isFunction()))
      throw new IllegalArgumentException("must push Function");

    synchronized(this) {
      Task task = new Task(func);
      last.next = task;
      last = task;
    }
  }


  /**
   * 运行消息队列, 直到队列清空,
   * 如果队列中任务抛出异常不会停止队列而是将错误发送到处理器
   */
  public void runUntilEmpty() {
    if (top == last) return;

    //
    // 这里是否需要完全锁住线程有待测试
    //
    synchronized (this) {
      Task task = pullFirst();
      while (task != null) {
        try {
          task.func.call(this);
        } catch (Exception e) {
          sendError(e);
        }
        task = pullFirst();
      }
    }
  }


  private Task pullFirst() {
    Task ret = null;
    if (top.next != null) {
      ret = top.next;
      top.next = ret.next;
      if (top.next == null) {
        last = top;
      }
    }
    return ret;
  }


  private void sendError(Exception e) {
    log.error(Tool.allStack(e));
    if (process == null) {
    } else {
      process.callMember("emit", "error", e);
    }
  }


  public class Task {
    private final ScriptObjectMirror func;
    private Task next;

    private Task(ScriptObjectMirror func) {
      this.func = func;
    }
  }
}
