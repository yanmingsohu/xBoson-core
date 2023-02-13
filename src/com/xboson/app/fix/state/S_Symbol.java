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
// 文件创建日期: 17-12-13 下午6:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_Symbol.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;


public class S_Symbol extends SState {

  protected int state = 0;
  private StringBuilder symbol;
  private int nameIndex;


  public S_Symbol(int nameIndex) {
    this.nameIndex = nameIndex;
  }


  public int read(byte ch) {
    if (state == 0 && Character.isJavaIdentifierStart(ch)) {
      state = 1;
      symbol = new StringBuilder();
      symbol.append((char)ch);
      return KEEP;
    }
    if (state == 1) {
      if (Character.isJavaIdentifierPart(ch)) {
        symbol.append((char)ch);
        return KEEP;
      } else {
        state = 0;
        data[nameIndex] = symbol.toString();
        return NEXT_AND_BACK;
      }
    }
    state = 0;
    return RESET;
  }
}
