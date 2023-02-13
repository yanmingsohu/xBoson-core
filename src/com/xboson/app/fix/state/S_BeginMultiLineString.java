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
// 文件创建日期: 17-12-26 上午11:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/state/S_BeginMultiLineString.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix.state;

import com.xboson.app.fix.SState;
import com.xboson.been.XBosonException;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class S_BeginMultiLineString extends SState {

  private List<String> lines;
  private StringBufferOutputStream out;
  private byte last;


  public S_BeginMultiLineString() {
    lines = new ArrayList<>();
    out = new StringBufferOutputStream(100);
  }


  public SState createBegin() {
    return new Begin();
  }


  /**
   * 初始状态机用于 js 注释/字符串范围 检测,
   * 其他状态机必须自己处理所有字符.
   */
  private class Begin extends SState {
    public int read(byte ch) {
      if (ch == '`') {
        lines.clear();
        S_BeginMultiLineString.this.out.clear();
        return NEXT;
      }
      return NOTHING;
    }
  }


  @Override
  public int read(byte ch) throws IOException {
    if (ch == '`') {
      if (last == '\\') {
        out.pop();
        out.write(ch);
        return KEEP;
      }

      lines.add(out.toString());
      build();
      lines.clear();
      out.clear();
      return NEXT;

    } else if (ch == '\r') {
      // do nothing
    } else if (ch == '\n') {
      lines.add(out.toString());
      out.clear();
    } else {
      out.write(ch);
    }

    last = ch;
    return KEEP;
  }


  private void build() {
    try (Writer out = new OutputStreamWriter(super.out)) {
      int size = lines.size();

      if (size > 0) {
        String line = lines.get(0);
        out.append("\"");
        printLine(out, line);
        out.append("\"");

        for (int i = 1; i < size; ++i) {
          line = lines.get(i);
          out.append("\n + \"\\n");
          printLine(out, line);
          out.append("\"");
        }
      }
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  private void printLine(Writer out, String line) throws IOException {
    boolean inEscape = false;

    for (int i=0; i<line.length(); ++i) {
      char c = line.charAt(i);

      if (c == '"' && inEscape == false) {
        out.append('\\');
      }

      if (c == '\\' && inEscape == false) {
        inEscape = true;
      } else {
        inEscape = false;
      }
      out.append(c);
    }
  }
}
