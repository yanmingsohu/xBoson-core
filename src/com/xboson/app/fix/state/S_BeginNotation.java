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
// 文件创建日期: 17-12-13 下午7:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_BeginNotation.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;


public class S_BeginNotation extends SState {
  private byte nota;
  private byte pch;
  private int state;


  public S_BeginNotation(char nota) {
    this.nota = (byte) nota;
    this.state = 0;
  }


  @Override
  public int read(byte ch) {
    if (state == 0) {
      if (ch == nota && (isNewCodeBlock(pch) || isOp(pch))) {
        state = 1;
        return BEGIN;
      } else {
        pch = ch;
        return NOTHING;
      }
    }
    if (state == 1) {
      state = 0;
      pch = 0;
      return NEXT_AND_BACK;
    }
    return NOTHING;
  }
}
