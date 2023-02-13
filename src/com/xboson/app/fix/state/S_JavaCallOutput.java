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
// 文件创建日期: 17-12-13 下午7:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_JavaCallOutput.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.ILastRunning;
import com.xboson.app.fix.SState;
import com.xboson.been.XBosonException;
import com.xboson.util.Tool;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class S_JavaCallOutput extends SState implements ILastRunning {

  private int objIndex, funcIndex, argsIndex;
  private final static boolean output_comment = false;


  public S_JavaCallOutput(int objNameIndex, int funcNameIndex, int argsIndex) {
    this.objIndex = objNameIndex;
    this.funcIndex = funcNameIndex;
    this.argsIndex = argsIndex;
  }

  @Override
  public int read(byte ch) {
    String obj = data[objIndex];
    String fun = data[funcIndex];
    String arg = data[argsIndex];

    try (Writer out = new OutputStreamWriter(super.out)) {
      out.append("__inner_call(");
      out.append("\"");
      out.append(fun);
      out.append("\", ");
      out.append(obj);

      if (! Tool.isNulStr(arg)) {
        out.append(", ");
        out.append(arg);
      }
      out.append(")");

      if (output_comment) {
        out.append("/* @");
        out.append(obj);
        out.append('.');
        out.append(fun);
        out.append('(');
        out.append(arg);
        out.append(") */");
      }
    } catch (IOException e) {
      throw new XBosonException(e);
    }

    return NEXT_AND_BACK;
  }
}
