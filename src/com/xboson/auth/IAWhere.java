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
// 文件创建日期: 17-11-15 下午3:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/IAWhere.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

/**
 * 该接口实现需要切实的实施权限检查;
 * 必须有默认构造函数.
 * toString 方法可以改变默认输出
 */
public interface IAWhere {

  /**
   * 在当前位置应用 who 对资源 res 的权限检查
   * 如果检查通过立即返回 true, 否则抛出异常或返回 false
   *
   * @param who 访问资源的主体
   * @param res 被访问的资源
   */
  boolean apply(IAWho who, IAResource res);


  /**
   * 返回 true 则超级用户总是通过权限检查
   * @return 默认实现返回 true
   */
  default boolean passAdmin() {
    return true;
  }

}
