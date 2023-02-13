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
// 文件创建日期: 17-12-13 下午7:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_DynArgument.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;


/**
 * 动态参数
 */
public class S_DynArgument extends SState {

  private StringBuilder arguments;
  private int state = 0;
  private int argIndex;


  public S_DynArgument(int argIndex) {
    this.argIndex = argIndex;
  }

  @Override
  public int read(byte ch) {
    if (state == 0) {
      if (isEndArguments(ch)) {
        data[argIndex] = null;
        return NEXT_AND_BACK;
      }

      arguments = new StringBuilder();
      state = 1;
    }
    else /* state == 1 */ {
      if (isEndArguments(ch)) {
        data[argIndex] = arguments.toString();
        state = 0;
        return NEXT_AND_BACK;
      }
    }
    arguments.append((char) ch);
    return KEEP;
  }


  public static boolean isEndArguments(byte ch) {
    return ch == ')' || ch == '}' || ch == '{' || ch == ';';
  }
}
