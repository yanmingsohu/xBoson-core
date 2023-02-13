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
// 文件创建日期: 2017年11月5日 上午10:52:43
// 原始文件路径: xBoson/src/com/xboson/script/SandboxFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.safe.BlockAllFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * 线程安全, 并对线程优化的沙箱工厂
 */
public class SandboxFactory {

  private static ThreadLocal<NashornScriptEngineFactory> seml = new ThreadLocal<NashornScriptEngineFactory>();
  private static Log log = LogFactory.create();
  private static BlockAllFilter blockall = new BlockAllFilter();


  /**
   * 创建一个独立的沙箱, 该沙箱对象与线程绑定.
   * 在同一个线程上调用该方法, 总是返回唯一的沙箱对象.
   *
   * @throws ScriptException
   */
  public static Sandbox create() throws ScriptException {
    ScriptEngine se = getEM().getScriptEngine(blockall);
    return new Sandbox(se);
  }


  public static NashornScriptEngineFactory getEM() {
    NashornScriptEngineFactory em = seml.get();
    if (em == null) {
      em = new NashornScriptEngineFactory();
      seml.set(em);
    }
    return em;
  }


  public static void version() {
    NashornScriptEngineFactory n = getEM();
    log.info("Script ENGINE:",
        "[" + n.getEngineName(), n.getEngineVersion() + "]",
        "[" + n.getLanguageName(), n.getLanguageVersion() + "]");
  }
}
