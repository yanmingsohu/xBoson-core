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
// 文件创建日期: 2017年11月5日 上午11:40:55
// 原始文件路径: xBoson/src/com/xboson/script/IJSObject.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;


/**
 * 当把这个对象注册到 BasicEnvironment 中, 只会在环境中生成唯一实例
 */
public interface IJSObject extends IVisitByScript {

  /**
   * 返回对象在环境中的变量名称
   */
  String env_name();

  /**
   * 返回对象中的属性是否应该被冻结
   */
  boolean freeze();

  /**
   * 当环境被创建时, 初始化自身, 该方法被环境调用
   */
  void init();

  /**
   * 当环境被销毁时, 调用
   */
  void destory();

}
