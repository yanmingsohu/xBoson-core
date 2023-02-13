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
// 文件创建日期: 18-7-19 上午11:11
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ScriptEnvConfiguration.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.lib.*;

import java.util.HashMap;
import java.util.Map;


public final class ScriptEnvConfiguration {

  /**
   * 返回环境配置脚本列表,
   * 这些脚本, 在创建沙箱时被调用, 可以绑定全局变量.
   * 加载根目录在 com.xboson.app.lib 下.
   */
  static String[] environment_script() {
    return new String[] {
            "array_sort_comparator.js",
            "sys_functions_impl.js",
            "string_functions.js",
            "compatible-syntax.js",
            "strutil.js",
            "ide.js",
            "pre-init.js",
    };
  }


  /**
   * 脚本全局对象, 在脚本中直接引用.
   */
  static Class[] global_library() {
    return new Class[] {
            MapImpl.class,
            DateImpl.class,
            ListImpl.class,
            ModuleHandleContext.class,
    };
  }


  /**
   * 脚本动态库, 在脚本中通过 require(..) 来引入
   */
  public static Map<String, Class> dynamic_library() {
    Map<String, Class> mod = new HashMap<>();
    mod.put("fs",        FsImpl.class);
    mod.put("mongodb",   MongoImpl.class);
    mod.put("shell",     Shell.class);
    mod.put("schedule",  Schedule.class);
    mod.put("pm",        PmImpl.class);
    mod.put("cluster",   Cluster.class);
    mod.put("fabric",    FabricImpl.class);
    mod.put("image",     ImageImpl.class);
    mod.put("xml",       XmlImpl.class);
    mod.put("ws",        WebService.class);
    mod.put("chain",     Chain.class);
    mod.put("digest",    Digest.class);
    mod.put("crypto",    CryptoImpl.class);
    mod.put("count",     CountImpl.class);
    mod.put("lock",      LockImpl.class);
    mod.put("config",    ConfigImpl.class);
    mod.put("docker",    DockerImpl.class);
    mod.put("pack",      PackImpl.class);
    mod.put("blas",      BlasImpl.class);
    mod.put("iot",       IOTImpl.class);
    mod.put("graph",     GraphImpl.class);
    return mod;
  }
}
