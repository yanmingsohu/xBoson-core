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
// 文件创建日期: 2017年11月7日 10:41
// 原始文件路径: xBoson/src/com/xboson/script/EnvironmentFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.script.lib.*;

import java.io.IOException;


public class EnvironmentFactory {

  private EnvironmentFactory() {}


  /**
   * 创建 nodejs 环境.
   */
  public static BasicEnvironment createBasic() throws IOException {
    BasicEnvironment env = new BasicEnvironment();
    configEnv(env);
    return env;
  }


  /**
   * 创建一个空环境
   */
  public static BasicEnvironment createEmptyBasic() {
    return new BasicEnvironment();
  }


  /**
   * 创建已经绑定了默认模块的 SysModules
   */
  public static SysModules createDefaultSysModules() throws IOException {
    SysModules sysmod = new SysModules();
    return setupNodeModules(sysmod);
  }


  /**
   * 将默认模块加载到 sys 中, 并返回 sysmod.
   */
  public static SysModules setupNodeModules(SysModules sysmod)
          throws IOException {
    Class processClass = com.xboson.script.lib.Process.class;

    sysmod.regClass("console",      Console.class);
    sysmod.regClass("path",         Path.class);
    sysmod.regClass("sys/buffer",   Buffer.class);
    sysmod.regClass("sys/uuid",     Uuid.class);
    sysmod.regClass("vm",           Vm.class);
    sysmod.regClass("sys/process",  processClass);
    sysmod.regClass("streamutil",   StreamUtil.class);
    sysmod.regClass("os",           OperatingSystem.class);

    SysModules.LibLoader sys = sysmod.open(processClass);
    sys.load("process",             "process.js");
    sys.load("sys/util",            "sysutil.js");
    sys.load("sys/json",            "JSON.js");
    sys.load("util",                "util.js");
    sys.load("assert",              "assert.js");
    sys.load("events",              "events.js");
    sys.load("buffer",              "buffer.js");
    sys.load("querystring",         "querystring.js");
    sys.load("punycode",            "punycode.js");
    sys.load("url",                 "url.js");
    sys.load("uuid",                "uuid.js");

    return sysmod;
  }


  /**
   * 将 env 配置为 nodejs 环境.
   */
  public static BasicEnvironment configEnv(BasicEnvironment env)
          throws IOException {
    SysModules sysmod = createDefaultSysModules();
    env.insertConfiger(sysmod);
    env.setEnvObject(Console.class);
    return env;
  }


  /**
   * 将 env 配置为 nodejs 环境.
   */
  public static BasicEnvironment configEnv(BasicEnvironment env, SysModules sysmod)
          throws IOException {
    env.insertConfiger(sysmod);
    env.setEnvObject(Console.class);
    return env;
  }

}
