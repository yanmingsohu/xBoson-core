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
// 文件创建日期: 18-5-26 下午8:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/NulLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

/**
 * 不做任何输出的 Log 对象, 用于重写 Log 的行为.
 */
public class NulLog extends Log {


  public NulLog(String name) {
    super(name);
  }


  public final void logs(final Level l, Object[] msg) {
    if (blocking(l)) {
      return;
    }
    dologs(l, msg);
  }


  /**
   * 实现日志的输出
   */
  protected void dologs(final Level l, Object[] msg) {}

}
