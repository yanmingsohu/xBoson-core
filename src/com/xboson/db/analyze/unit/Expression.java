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
// 文件创建日期: 17-12-10 下午12:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/analyze/unit/Expression.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.analyze.unit;

import com.xboson.db.analyze.AbsUnit;
import com.xboson.db.analyze.SqlContext;


/**
 * 该对象线程安全.
 * 可通过 setData() 改变表达式的前缀.
 * 多线程复用时, 必须保证 setData() 总是被调用, 否则
 * 会残留上一个线程设置的值.
 */
public class Expression extends AbsUnit<String> {
  private final String exp;


  public Expression(String exp) {
    this.exp = exp;
  }


  @Override
  public void setData(String d) {
    throw new UnsupportedOperationException();
  }


  @Override
  public String getData() {
    return exp;
  }


  @Override
  public String stringify(SqlContext ctx) {
    Object rep = ctx.get(this);
    if (rep == null) {
      return exp;
    } else {
      return rep.toString();
    }
  }
}
