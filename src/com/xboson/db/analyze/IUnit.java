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
// 文件创建日期: 17-12-10 上午10:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/IUnit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze;

/**
 * 解析后的语法单位;
 * 注意线程安全: 解析后的语法树会被缓存, 并循环利用.
 */
public interface IUnit<T> {

  /**
   * 给单位设置参数, 单位需要自行解析 sql 字符串为自身变量.
   * 注意: 该方法的实现在必要时使用线程级变量.
   */
  void setData(String d);

  /**
   * 获取值;
   * 注意: 该方法的实现在必要时使用线程级变量.
   * 该方法返回的值未必与 setData 设置的值相同, 不保证对称.
   */
  T getData();

  /**
   * 设置父级关键字
   */
  void setParent(IUnit n);
  IUnit getParent();

  void setOperating(UnitOperating t);
  UnitOperating getOperating();

  /**
   * 输出为 sql 时被调用
   */
  String stringify(SqlContext ctx);

  /**
   * 锁定当前组件, set 方法默认将抛出异常,
   * 由于组件可以被多线程访问, 实现需要在必要时检查锁, 决定 set 的行为.
   */
  void lock();
  boolean isLocked();
}
