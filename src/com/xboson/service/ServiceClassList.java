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
// 文件创建日期: 2017年11月3日 下午4:39:48
// 原始文件路径: xBoson/src/com/xboson/service/ServiceClassList.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;


public interface ServiceClassList {

  /**
   * 所有服务列表, 服务的路径使用注解设定
   */
  Class<?>[] list = new Class<?>[] {
					App.class,
					UserService.class,
          Captcha.class,
          OAuth2.class,
          OlderApi.class,
          Witness.class,
          OpenApp.class,
  };

}
