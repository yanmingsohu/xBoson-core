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
// 文件创建日期: 18-1-19 下午7:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/Process.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.script.SandboxFactory;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;


public class Process {

  public long[] hrtime() {
    return new long[] { System.currentTimeMillis() / 1000, System.nanoTime() };
  }


  public String engineVersion() {
    NashornScriptEngineFactory ef = SandboxFactory.getEM();
    return ef.getEngineVersion();
  }


  public String languageVersion() {
    NashornScriptEngineFactory ef = SandboxFactory.getEM();
    return ef.getLanguageVersion();
  }


  /**
   * 多线程同步, 在 lockTatget 对象上枷锁, 并执行 callback 方法, callback 返回后解锁.
   * 如果将 js 对象作为锁对象, js 对象会被 ScriptObjectMirror 包装, 直接作为锁不起作用.
   *
   * @param lockTatget
   * @param callback
   * @return 返回 callback 返回的对象.
   */
  public Object lock(Object lockTatget, ScriptObjectMirror callback) {
    synchronized (lockTatget) {
      return callback.call(lockTatget);
    }
  }


  public Object lock(ScriptObjectMirror lt, ScriptObjectMirror cb) {
    return lock(ScriptUtils.unwrap(lt), cb);
  }
}
