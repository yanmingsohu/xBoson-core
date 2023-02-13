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
// 文件创建日期: 17-12-13 下午6:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_Space.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;


/**
 * 可以有零个或多个空格, 返回后指针指向空格的后一个字符,
 * 不包含换行, 换行使用 S_SpaceEnter.
 */
public class S_Space extends SState {
  public int read(byte ch) {
    if (ch == ' ' || ch == '\t') {
      return KEEP;
    }
    return NEXT_AND_BACK;
  }
}
