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
// 文件创建日期: 17-12-4 上午10:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/CodeFormater.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 代码格式化工具
 */
public class CodeFormater {

  private static final char ENTER = '\n';
  private static final String NULSTR = "";

  private List<Integer> line_saved;
  private Reader reader;
  private StringBuilder strbuf;


  public CodeFormater(ByteBuffer code) {
    ReaderSet r = new ReaderSet();
    r.add(code);
    this.reader = r;
  }


  public CodeFormater(Reader reader) {
    this.reader = reader;
  }


  public CodeFormater(String code) {
    this(new StringReader(code));
  }


  public String readLine(int i) {
    try {
      String line = readOriginalLine(i);
      if (line == null)
        return null;

      StringBuilder out = new StringBuilder();
      outline(out, line, i);
      return out.toString();

    } catch (Exception e) {
      e.printStackTrace();
      throw new XBosonException(e);
    }
  }


  public int totalLine() {
    if (line_saved == null) {
      parseLine();
    }
    return line_saved.size();
  }


  /**
   * 行数从 1 开始
   */
  private String readOriginalLine(int i) {
    if (line_saved == null) {
      parseLine();
    }
    if (i >= line_saved.size()) {
      return null;
    }
    if (i == 0) {
      return null;
    }

    int begin = line_saved.get(i - 1) + 1;
    int end = line_saved.get(i);
    int len = end - begin;

    if (len <= 0) {
      return NULSTR;
    }

    char[] buf = new char[len];
    strbuf.getChars(begin, end, buf, 0);
    return new String(buf);
  }


  private void parseLine() {
    line_saved = new ArrayList<>(100);
    line_saved.add(-1);
    strbuf = new StringBuilder();

    try {
      reader.reset();
      int ch = reader.read();
      int i = 0;
      while (ch >= 0) {
        strbuf.append((char) ch);
        if (ch == ENTER) {
          line_saved.add(i);
        }
        ++i;
        ch = reader.read();
      }
      line_saved.add(i);
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 输出代码到 out, 限制行数从 begin 到 end.
   *
   * @param out 输出流
   * @param begin 从 1 开始, 包括第 begin 行
   * @param end 结束行, 不包括第 end 行, <0 则一直输出到最后
   * @throws IOException
   */
  public void printCode(Appendable out, int begin, int end) throws IOException {
    String line;
    int count = begin;
    for (;;) {
      line = readOriginalLine(count);
      if (line != null) {
        outline(out, line, count);
        out.append('\n');
        ++count;
      } else {
        break;
      }
      if (end > begin && count >= end) {
        break;
      }
    }
  }


  private void outline(Appendable out, String line_code, int count) throws IOException {
    String c = (count<1000 ? (count < 100 ? (count < 10?
            "   " :"  ") :" ") :"")+ count;
    out.append("/* ");
    out.append(c);
    out.append(" */   ");
    out.append(line_code);
  }


  /**
   * 打印所有代码
   * @see #printCode(Appendable, int, int)
   */
  public void printCode(Appendable out) throws IOException {
    printCode(out, 1, -1);
  }


  public Exception createSourceException(String file_name, int line) {
    if (line_saved == null) {
      parseLine();
    }
    int begin = Math.max(1, line - 2);
    int end = Math.min(line_saved.size(), line + 3);

    StringBuilder out = new StringBuilder();
    out.append(file_name);
    out.append(" [");
    out.append(line);
    out.append(']');
    out.append('\n');

    try {
      printCode(out, begin, end);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return new JSSource(out.toString());
  }


  /**
   * 在错误堆栈中输出源代码
   */
  public class JSSource extends XBosonException {
    private JSSource(String code) {
      super(code);
      setStackTrace(new StackTraceElement[0]);
    }
  }
}
