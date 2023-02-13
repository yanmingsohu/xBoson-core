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
// 文件创建日期: 17-12-25 下午3:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/AbsModules.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsModules implements IConfigurableModuleProvider {


  /**
   * 该方法直接调用两个参数的 getModule
   * @see #getModule(String, Module)
   */
  public final Module getModule(String name) {
    return getModule(name, null);
  }


  /**
   * 返回所有可能的脚本加载路径
   * @param path_name 以该目录为基础
   * @return 路径数组
   */
  public static String[] get_module_paths(String path_name) {
    if (path_name == null) return null;

    List<String> paths = new ArrayList<>();
    paths.add(path_name);
    int i = path_name.lastIndexOf("/", path_name.length());

    while (i >= 0) {
      paths.add(path_name.substring(0, i) + MODULE_NAME);
      i = path_name.lastIndexOf("/", i-1);
    }
    return paths.toArray(new String[paths.size()]);
  }

}
