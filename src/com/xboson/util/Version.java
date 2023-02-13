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
// 文件创建日期: 17-11-25 下午12:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Version.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

public final class Version {

  public final static String xBoson = "2.72";
  public final static String Name = "智慧大数据开放平台";

  /**
   * 公钥 CRC, 不一致程序不能启动,
   * 控制台会输出 crc 的正确数字, 将数字替换 0xNL 中的 N 即可.
   * 一旦修改, 会导致使用该属性加密的数据不可用 (需刷新 redis).
   */
  public final static long PKCRC = Long.MAX_VALUE- 0x7fffffff9ca646f7L;

}
