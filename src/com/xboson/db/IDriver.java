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
// 文件创建日期: 17-11-14 上午9:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/IDriver.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

/**
 * 驱动接口的实现负责创建到数据库的直接链接,
 * 一旦连接完成, 并不做后续的管理.
 */
public interface IDriver extends IDialect {

  /**
   * 返回驱动类的 className
   */
  String driverClassName();


  /**
   * 返回该驱动的唯一且简化的名字
   */
  String name();


  /**
   * 返回该驱动的唯一 ID 值
   */
  int id();


  /**
   * 使用连接配置创建到数据库的连接 URL
   */
  String getUrl(ConnectConfig config);


  /**
   * 返回数据库的默认连接端口
   */
  int port();

}
