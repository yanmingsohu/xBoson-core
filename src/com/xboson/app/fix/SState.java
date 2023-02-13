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
// 文件创建日期: 17-12-13 下午6:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/SState.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

import com.xboson.app.fix.state.S_Operator;

import java.io.OutputStream;


public abstract class SState implements ISState {
  protected String[] data;
  protected OutputStream out;

  @Override
  public void setData(String[] d) {
    this.data = d;
  }

  @Override
  public void setOutput(OutputStream out) {
    this.out = out;
  }


  /**
   * ch 是一个新代码块的结束符号返回 true
   */
  public static boolean isNewCodeBlock(byte ch) {
    return ch == ' ' || ch == '\n' || ch == '\t'
            || ch == ';' || ch == '}' || ch == '\r';
  }


  /**
   * 是运算符符号返回 true
   * @see S_Operator#isOperator(char)
   */
  public static boolean isOp(byte c) {
    return S_Operator.isOperator((char) c);
  }
}
