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
// 文件创建日期: 17-12-4 上午9:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/JScriptException.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.CodeFormater;
import com.xboson.util.Tool;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
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
  private String fileName;
  private int lastLine;
  private CodeFormater cf;
  private Log log = LogFactory.create("Script.Inner.ERR");


  /**
   * @see JScriptException#JScriptException(Throwable, CodeFormater, int)
   */
  public JScriptException(Throwable fail) {
    this(fail, null, OffsetLineStack.offset);
  }


  /**
   * @see JScriptException#JScriptException(Throwable, CodeFormater, int)
   */
  public JScriptException(Throwable fail, byte[] code, String fileName) {
    this(fail, new CodeFormater(ByteBuffer.wrap(code)), OffsetLineStack.offset);
    setFileName(fileName);
  }


  /**
   * 该方法将出错行的偏移设置为全局 OffsetLineStack
   * @see JScriptException#JScriptException(Throwable, CodeFormater, int)
   * @see OffsetLineStack
   */
  public JScriptException(Throwable fail, Reader code) {
    this(fail, new CodeFormater(code), OffsetLineStack.offset);
  }


  public JScriptException(Throwable fail, Reader code, String filename) {
    this(fail, new CodeFormater(code), OffsetLineStack.offset);
    setFileName(filename);
  }


  /**
   * 该构造方法会过滤 fail 中无关的错误堆栈项.
   * 并设置一个文件行偏移.
   */
  public JScriptException(Throwable fail, CodeFormater cf, int offset) {
    super(fail.getMessage());
    this.lastFileName = null;
    this.lastLine = -1;
    this.cf = cf;
    setLineOffset(offset);
    collect_trace(fail);
    log.debug(Tool.allStack(fail));
  }


  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  /**
   * 当打印错误堆栈时, 可以设置一个文件行偏移.
   */
  public void setLineOffset(int l) {
    line_offset = l;
  }


  private void collect_trace(Throwable fail) {
    if (fail instanceof JScriptException) {
      setStackTrace(fail.getStackTrace());
      initCause(fail.getCause());
      return;
    }

    List<StackTraceElement> trace = new ArrayList<>();
    format_js_stack(fail, trace);

    if (cf != null && lastLine >= 0) {
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
    if (e instanceof ScriptException) {
      ScriptException se = (ScriptException) e;
      setNameAndLine(se.getFileName(), se.getLineNumber());
    }

    StackTraceElement[] st = e.getStackTrace();
    for (int i=0; i<st.length; ++i) {
      if (ECMAErrors.isScriptFrame(st[i])) {
        trace.add( clear_trace(st[i]) );
      }
    }

    Throwable t = e.getCause();
    if (t != null) {
      format_js_stack(t, trace);
    }
  }


  private StackTraceElement clear_trace(StackTraceElement in) {
    setNameAndLine(in.getFileName(), in.getLineNumber());
    StackTraceElement ret = new StackTraceElement(
            CLASS_NAME,
            in.getMethodName(),
            in.getFileName(),
            in.getLineNumber() + line_offset);
    return ret;
  }


  private void setNameAndLine(String filename, int line) {
    if (lastFileName != null)
      return;

    if (this.fileName != null && filename.indexOf(this.fileName) < 0)
      return;

    this.lastLine = line;
    this.lastFileName = filename;
  }
}
