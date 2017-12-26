////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class S_BeginMultiLineString extends SState {

  private int state = 0;
  private List<String> lines;
  private StringBufferOutputStream out;


  public S_BeginMultiLineString() {
    lines = new ArrayList<>();
    out = new StringBufferOutputStream(100);
  }


  @Override
  public int read(byte ch) {
    if (state == 0) {
      if (ch == '`') {
        lines.clear();
        out.clear();
        state = 1;
        return NEXT;
      }
      return NOTHING;
    } else {
      if (ch == '`') {
        lines.add(out.toString());
        build();
        lines.clear();
        out.clear();
        state = 0;
        return NEXT;
      } else if (ch == '\r') {
        // do nothing
      } else if (ch == '\n') {
        lines.add(out.toString());
        out.clear();
      } else {
        try {
          out.write(ch);
        } catch (IOException e) {
          throw new XBosonException.IOError(e);
        }
      }
      return NEXT;
    }
  }


  private void build() {
    try (Writer out = new OutputStreamWriter(super.out)) {
      int size = lines.size();

      if (size > 0) {
        String line = lines.get(0);
        out.append("\"");
        out.append(line);
        out.append("\"");

        for (int i = 1; i < size; ++i) {
          line = lines.get(i);
          out.append("\n + \"\\n");
          out.append(line);
          out.append("\"");
        }
      }
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }
}
