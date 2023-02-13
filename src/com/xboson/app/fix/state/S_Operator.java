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
// 文件创建日期: 17-12-14 下午12:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_Operator.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;
import com.xboson.util.JavaConverter;
import com.xboson.util.Tool;

import java.util.Set;


/**
 * 运算符
 */
public class S_Operator extends SState {

  private static final Set<Character> op = JavaConverter.arr2set(new Character[] {
          '+', '-', '*', '/', '=', '>', '<', '!', '&', '|', '%', ',', ';'
  });


  /**
   * c 是运算符返回 true
   */
  public static boolean isOperator(char c) {
    return op.contains(c);
  }


  @Override
  public int read(byte ch) {
    if (isOperator((char) ch)) {
      return NEXT;
    }
    return RESET;
  }
}
