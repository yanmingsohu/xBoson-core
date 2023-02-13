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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_For_Output.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.ILastRunning;
import com.xboson.app.fix.SState;
import com.xboson.been.XBosonException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * for (row in MAP) { <br/>
 * 重写为: <br/>
 * for (var row__index in MAP) { var row = __createKVString(row__index, MAP[row__index]);
 */
public class S_For_Output extends SState implements ILastRunning {

  private int varIndex, expIndex;


  public S_For_Output(int varName, int expName) {
    this.varIndex = varName;
    this.expIndex = expName;
  }


  @Override
  public int read(byte ch) {
    String keyName = data[varIndex];
    String objName = data[expIndex];
    String indexName = keyName + "__index";

    try (Writer out = new OutputStreamWriter(super.out)) {
      out.append("for (var ");
      out.append(indexName);
      out.append(" in ");
      out.append(objName);
      out.append(") { var ");
      out.append(keyName);
      out.append(" = __createKVString(");
      out.append(indexName);
      out.append(", ");
      out.append(objName);
      out.append("[");
      out.append(indexName);
      out.append("]);");
    } catch (IOException e) {
      throw new XBosonException(e);
    }
    return NEXT_AND_BACK;
  }
}
