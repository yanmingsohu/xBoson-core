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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/IAWho.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

/**
 * 是谁? 是用户? 是机构? 是第三方应用?
 * 这是在检查权限时的主体.
 */
public interface IAWho {

  /**
   * 该方法返回非 null 的唯一身份识别码, 否则认为识别失败;
   * 该方法直接抛出的任何异常都导致认证失败.
   */
  String identification();


  /**
   * 是根用户返回 true, 根用户不受任何权限制约.
   */
  boolean isRoot();

}
