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
// 文件创建日期: 17-11-20 上午8:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/ITimeout.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

/**
 * 当持久化对象需要在超时后从 redis 中删除, 则实现该接口
 */
public interface ITimeout {

  /**
   * 对象 wake 后检查该方法的返回值, 如果返回 true 则从持久化池中删除.
   */
  boolean isTimeout();

}
