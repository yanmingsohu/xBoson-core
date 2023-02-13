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
// 文件创建日期: 17-12-14 下午12:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_VirtualAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;
import com.xboson.been.XBosonException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class S_VirtualAttr extends SState {

  private int objIndex;
  private int attrIndex;


  public S_VirtualAttr(int objIndex, int attrIndex) {
    this.objIndex = objIndex;
    this.attrIndex = attrIndex;
  }


  @Override
  public int read(byte ch) {
    String obj = data[objIndex];
    String attr = data[attrIndex];

    try (Writer out = new OutputStreamWriter(super.out)) {
      out.append("__virtual_attr(");
      out.append(obj);
      out.append(", \"");
      out.append(attr);
      out.append("\")");
    } catch (IOException e) {
      throw new XBosonException(e);
    }

    return END;
  }
}
