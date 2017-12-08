////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-4 上午9:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/JScriptException.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.XBosonException;
import com.xboson.util.CodeFormater;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.ECMAException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * JS 脚本异常.
 * (若多层包装可能会丢失堆栈, 应该在 cache 时针对 ECMAException 做单独处理)
 *
 * @see ECMAException
 */
public class JScriptException extends XBosonException {

  private static final String CLASS_NAME = "<javascript>";
  private int line_offset = 0;
  private String lastFileName;
  private int lastLine;


  /**
   * @see JScriptException#JScriptException(Exception, ByteBuffer, int)
   */
  public JScriptException(Exception fail) {
    this(fail, null, 0);
  }


  /**
   * @see JScriptException#JScriptException(Exception, ByteBuffer, int)
   */
  public JScriptException(Exception fail, ByteBuffer code) {
    this(fail, code, 0);
  }


  /**
   * 该构造方法会过滤 fail 中无关的错误堆栈项.
   * 并设置一个文件行偏移.
   */
  public JScriptException(Exception fail, ByteBuffer code, int offset) {
    super(fail.getMessage());
    lastFileName = null;
    lastLine = -1;
    setLineOffset(offset);
    collect_trace(fail, code);
  }


  /**
   * 当打印错误堆栈时, 可以设置一个文件行偏移.
   */
  public void setLineOffset(int l) {
    line_offset = l;
  }


  private void collect_trace(Exception fail, ByteBuffer code) {
    if (fail instanceof JScriptException) {
      setStackTrace(fail.getStackTrace());
      initCause(fail.getCause());
      return;
    }

    List<StackTraceElement> trace = new ArrayList<>();
    format_js_stack(fail, trace);

    if (code != null && lastLine >= 0) {
      CodeFormater cf = new CodeFormater(code);
      Exception source = cf.createSourceException(lastFileName, lastLine);
      if (source != null) {
        super.initCause(source);
      }
    }

    StackTraceElement[] tc = new StackTraceElement[ trace.size() ];
    trace.toArray(tc);
    this.setStackTrace(tc);
  }


  private void format_js_stack(Throwable e, List<StackTraceElement> trace) {
    if (e instanceof ECMAException) {
      ECMAException ecma = (ECMAException) e;
      StackTraceElement[] st = ecma.getStackTrace();

      for (int i=0; i<st.length; ++i) {
        if (ECMAErrors.isScriptFrame(st[i])) {
          trace.add( clear_trace(st[i]) );
        }
      }
    }

    Throwable t = e.getCause();
    if (t != null) {
      format_js_stack(t, trace);
    }
  }


  private StackTraceElement clear_trace(StackTraceElement in) {
    if (lastFileName == null) {
      lastFileName = in.getFileName();
    }
    if (lastLine < 0) {
      lastLine = in.getLineNumber();
    }
    StackTraceElement ret = new StackTraceElement(
            CLASS_NAME,
            in.getMethodName(),
            in.getFileName(),
            in.getLineNumber() + line_offset);
    return ret;
  }
}
