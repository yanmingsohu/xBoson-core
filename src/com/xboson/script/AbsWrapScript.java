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
// 文件创建日期: 17-12-22 下午3:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/AbsWrapScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;


/**
 * 对 js 脚本的封装, 持有一个 Module 对象.
 */
public abstract class AbsWrapScript {

  protected final Module module;
  protected final String filename;


  public AbsWrapScript(String filename) {
    this.module = new Module();
    this.filename = filename;
  }


  /**
   * 编译当前脚本
   */
  public abstract void compile(Sandbox box);


  /**
   * 运行编译好的脚本, 并初始化模块对象
   */
  public abstract Object initModule(ICodeRunner crun);


  /**
   * 返回模块, 即使没有初始化, 也会返回没有属性的 Module.
   */
  public Module getModule() {
    return module;
  }


  /**
   * 返回文件名
   */
  public String getFilename() {
    return filename;
  }

}
