////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

  private static final byte ENTER = (byte)'\n';
  private ByteBuffer code;
  private List<Integer> line_saved;


  public CodeFormater(ByteBuffer code) {
    this.code = code;
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

    int begin = line_saved.get(i-1) + 1;
    int end   = line_saved.get(i) - 1;
    return new String(code.array(), begin, end-begin, IConstant.CHARSET);
  }


  private void parseLine() {
    line_saved = new ArrayList<>(100);
    line_saved.add(-1);
    byte[] buf = code.array();

    for (int i=0; i<buf.length; ++i) {
      if (buf[i] == ENTER) {
        line_saved.add(i);
      }
    }
    line_saved.add(buf.length);
  }


  /**
   * 输出代码到 out, 限制行数从 begin 到 end.
   *
   * @param out 输出流
   * @param begin 从 0 开始, 包括第 begin 行
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
  public void printCode(Writer out) throws IOException {
    printCode(out, 0, -1);
  }


  public Exception createSourceException(String file_name, int line) {
    if (line_saved == null) {
      parseLine();
    }
    int begin = Math.max(1, line - 2);
    int end = Math.min(line_saved.size(), line + 3);

    StringBuilder out = new StringBuilder();
    out.append(file_name);
    out.append('[');
    out.append(begin);
    out.append(',');
    out.append(end-1);
    out.append(']');
    out.append("\n");

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
  class JSSource extends XBosonException {
    private JSSource(String code) {
      super(code);
      setStackTrace(new StackTraceElement[0]);
    }
  }
}