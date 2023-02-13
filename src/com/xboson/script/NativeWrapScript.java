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
// 文件创建日期: 17-12-22 下午3:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/NativeWrapScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.XBosonException;


/**
 * 这是用 java 代码写的 js 模块的包装
 */
public class NativeWrapScript extends AbsWrapScript {

  private final Class<?> clazz;
  private Object moduleInstance;


  public NativeWrapScript(String filename, Class<?> clazz) {
    super(filename);
    this.clazz = clazz;
  }


  public NativeWrapScript(String filename, Object mod) {
    super(filename);
    this.clazz = mod.getClass();
    this.moduleInstance = mod;
  }


  @Override
  public void compile(Sandbox box) {
  }


  @Override
  public Object initModule(ICodeRunner crun) {
    try {
      if (moduleInstance == null) {
        moduleInstance = clazz.newInstance();
      }
      module.exports = moduleInstance;
      module.loaded = true;
      return moduleInstance;
    } catch (Exception e) {
      throw new XBosonException("Create Java Module Fail ("+ filename +")", e);
    }
  }
}
