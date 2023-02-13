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
/* CatfoOD yanming-sohu@sohu.com Q.412475540 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 2017年11月5日 上午11:50:29
// 原始文件路径: xBoson/src/com/xboson/script/lib/Console.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.log.Level;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.JSObject;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Arrays;


public class Console extends JSObject {

  private Log log;


  public Console() {
    log = LogFactory.create("script.console");
  }


  public Console(String name) {
    log = LogFactory.create(name);
  }


  public Console create(String name) {
    if (name != null) {
      return new Console(name);
    } else {
      return new Console();
    }
  }


  @Override
  public String env_name() {
    return "console";
  }


  public Console info(Object ...msg) {
    log.logs(Level.INFO, join(msg));
    return this;
  }


  public Console log(Object ...msg) {
    log.logs(Level.INFO, join(msg));
    return this;
  }


  public Console debug(Object ...msg) {
    log.logs(Level.DEBUG, join(msg));
    return this;
  }


  public Console error(Object ...msg) {
    log.logs(Level.ERR, join(msg));
    return this;
  }


  public Console warn(Object ...msg) {
    log.logs(Level.WARN, join(msg));
    return this;
  }


  public Console fatal(Object ...msg) {
    log.logs(Level.FATAL, join(msg));
    return this;
  }


  public Console trace(Object ...msg) {
    return debug(msg);
  }


  private Object[] join(Object ...msg) {
    Object[] ret = new Object[msg.length];

    for (int i=0; i<msg.length; ++i) {
      if (msg[i] instanceof ScriptObjectMirror) {
        ScriptObjectMirror js = (ScriptObjectMirror) msg[i];

        if (!js.isEmpty()) {
          ret[i] = Tool.beautifyJson(ScriptObjectMirror.class, js);
        } else {
          ret[i] = js.toString();
        }
      }
      else {
        ret[i] = msg[i];
      }
    }
    return ret;
  }
}
