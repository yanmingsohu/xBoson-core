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
// 文件创建日期: 17-12-15 下午6:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ApiTypes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;


import com.xboson.been.ApiCall;


public enum ApiTypes {

  Production("/API/prod", "r"),
  Development("/API/dev", "d");


  /**
   * 与 full 相同 (!拼写错误)
   */
  public final String eventPrifix;

  /**
   * 一个字符的短描述
   */
  public final String flag;

  /**
   * 应用类型完整描述
   */
  public final String full;


  ApiTypes(String eventPrefix, String flag) {
    this.full = eventPrefix;
    this.eventPrifix = eventPrefix;
    this.flag = flag;
  }


  /**
   * 's' 调试状态标记
   *    d：执行最新代码并返回调试信息，
   *    r：执行已发布代码
   */
  public static ApiTypes of(ApiCall ac) {
    if (Development.flag.equalsIgnoreCase(ac.call.req.getParameter("s"))) {
      return Development;
    }
    return Production;
  }


  public String toString() {
    return full;
  }
}
