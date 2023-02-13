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
// 文件创建日期: 18-1-9 下午6:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/config/SerialFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.config;

/**
 * 决定使用何种格式的配置文件
 */
public final class SerialFactory {

  public static final String CONFIG_DIR 	= "/xBoson-config";
  public static final String LOG_DIR 			= "/logs";

  private final static IConfigSerialization instance = new YamlConfigImpl();


  public static IConfigSerialization get() {
    return instance;
  }


  private SerialFactory() {}
}
