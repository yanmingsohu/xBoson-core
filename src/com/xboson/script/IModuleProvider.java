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
// 文件创建日期: 2017年11月6日 10:21
// 原始文件路径: xBoson/src/com/xboson/script/IModuleProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;


/**
 * 模块加载器
 */
public interface IModuleProvider extends IVisitByScript {

  int LOADER_ID_APPLICATION = 1;
  int LOADER_ID_SYS_MODULE  = 2;
  int LOADER_ID_NODE_MODULE = 3;

  String MODULE_NAME = "/node_modules";


  /**
   * 从模块路径返回模块, 并且应该将模块缓存给 Application,
   * 在必要时从 applyMod 模块中提取加载路径, 并尝试从这些路径中加载模块.
   * 必要时该方法必须同步.
   *
   * @param name 模块路径
   * @param applyMod 加载模块的模块
   * @return 如果模块加载器找不到模块应该返回 null
   * @see Application
   */
  Module getModule(String name, Module applyMod);

}
