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
// 文件创建日期: 2017年11月9日 11:53
// 原始文件路径: xBoson/src/com/xboson/util/IConversion.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

public interface IConversion<IN, OUT> {
  /**
   * 转换数据
   * @param obj
   * @return 返回转换后的值, 如果不许要转换应返回 obj 本身
   */
  OUT value(IN obj);

  /**
   * 类型转换
   * @param _class
   * @return 返回转换后的类型, 如果不需要转换应返回 _class 本身
   */
  Class<?> type(Class<?> _class);
}
