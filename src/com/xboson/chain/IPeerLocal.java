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
// 文件创建日期: 18-7-19 上午10:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/IPeerLocal.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;


/**
 * 本地接口,
 */
public interface IPeerLocal {

  /**
   * 注册签名机提供商, 默认什么都不做
   */
  default void registerSignerProvider(ISignerProvider sp) {}


  /**
   * 等待操作完成
   */
  void waitOver();


  /**
   * 同步所有链到最新状态, 默认什么都不做.
   */
  default void startSynchronizeThread() {}

}
