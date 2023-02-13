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
// 文件创建日期: 18-5-26 下午5:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/SqlContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

import java.util.HashMap;
import java.util.Map;


/**
 * 在 sql 序列化时, 允许针对 IUnit 组件设置变量, 用来改变原始 sql.
 * 该对象在每次序列化操作前创建, 线程不安全.
 */
public class SqlContext {

  private Map<IUnit, Object> map;


  public SqlContext() {
    map = new HashMap<>();
  }


  /**
   * 设置组件 u 的数据
   */
  public void set(IUnit u, Object data) {
    map.put(u, data);
  }


  /**
   * 获取针对组件 u 设置的数据
   */
  public Object get(IUnit u) {
    return map.get(u);
  }


  public void clear() {
    map.clear();
  }
}
