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
// 文件创建日期: 17-12-14 下午12:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_BeginSymbol.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

public class S_BeginSymbol extends S_Symbol {

  public S_BeginSymbol(int nameIndex) {
    super(nameIndex);
  }


  @Override
  public int read(byte ch) {
    if (state == 0) {
      int op = super.read(ch);
      switch (op) {
        case KEEP:  return BEGIN;
        case RESET: return NOTHING;
        default:    return op;
      }
    }
    else {
      return super.read(ch);
    }
  }
}
