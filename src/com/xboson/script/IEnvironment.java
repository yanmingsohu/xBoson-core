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
// 文件创建日期: 2017年11月5日 上午11:40:08
// 原始文件路径: xBoson/src/com/xboson/script/IEnvironment.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

/**
 * 将对象绑定到全局,
 * 每个环境的实例中的数据都是缓存的, 除非创建新的实例, 
 * 否则环境中的变量可能在配置新的沙箱时被修改数据.
 */
public interface IEnvironment extends IConfigSandbox {

  /**
   * 插入一个配置器, 当应用启动后将配置到应用的沙箱中去.
   */
  void insertConfiger(IConfigSandbox cs);


  /**
   * 将对象注册到全局, 可以在上下文直接引用, 在必要时初始化唯一实例
   * @param jsobj
   */
  void setEnvObject(Class<? extends IJSObject> jsobj);


  /**
   * @see #setEnvObject(Class)
   * @param list 将所有对象注册到全局.
   */
  void setEnvObjectList(Class<? extends IJSObject>[] list);


  /**
   * 释放内存
   */
  void destory();

}
