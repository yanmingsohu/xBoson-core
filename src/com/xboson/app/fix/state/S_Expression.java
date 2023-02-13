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
// 文件创建日期: 17-12-13 下午6:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_Expression.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;


public class S_Expression extends SState {
  private StringBuilder exp;
  private int state = 0;
  private int savetoIndex;
  private int nested_count = 0;

  public S_Expression(int savetoIndex) {
    this.savetoIndex = savetoIndex;
  }

  @Override
  public int read(byte ch) {
    if (state == 0) {
      exp = new StringBuilder();
      state = 1;
    }

    if (ch == '(') {
      ++nested_count;
    }
    else if (ch == ')') {
      if (nested_count > 0) {
        --nested_count;
      } else {
        state = 0;
        data[savetoIndex] = exp.toString();
        return NEXT_AND_BACK;
      }
    }
    else if (ch == '\n') {
      state = 0;
      nested_count = 0;
      return RESET;
    }
    exp.append((char)ch);
    return KEEP;
  }
}
