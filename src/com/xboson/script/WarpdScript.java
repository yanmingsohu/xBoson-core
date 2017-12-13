////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月5日 上午9:56:07
// 原始文件路径: xBoson/src/com/xboson/script/WarpdScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.util.ReaderSet;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.nio.ByteBuffer;


public class WarpdScript {

  private static final String S_WRAP0 = "__warp_main(function(" +
          "require, module, __dirname , __filename, exports, console) {";
  private static final String S_WRAP1 = "\n})";

  private AbstractJSObject jso;
  private Sandbox box;
  private Module module;
  private CompiledScript cs;
  private String filename;
  private ICodeRunner crun;
  private Object warpreturn;
  private ReaderSet code_reader;


  private WarpdScript(Sandbox box, String filename) {
    this.box  	  = box;
    this.module   = new Module();
    this.filename = filename;
    this.code_reader = new ReaderSet();
  }


  WarpdScript(Sandbox box, ByteBuffer code, String filename)
          throws ScriptException {
    this(box, filename);
    code_reader.add(S_WRAP0);
    code_reader.add(code);
    code_reader.add(S_WRAP1);
    warp();
  }


  WarpdScript(Sandbox box, String code, String filename)
          throws ScriptException {
    this(box, filename);
    code_reader.add(S_WRAP0);
    code_reader.add(code);
    code_reader.add(S_WRAP1);
    warp();
  }


  private void warp() throws ScriptException {
    box.setFilename(filename);
    cs = box.compile(code_reader);
  }


  public Object call() {
    try {
      jso = (AbstractJSObject) cs.eval();
      warpreturn = jso.call(module, module, crun);
      module.loaded = true;
      return warpreturn;

    } catch (ECMAException ec) {
      throw new JScriptException(ec, code_reader);

    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  public Module getModule() {
    return module;
  }


  public void setCodeRunner(ICodeRunner crun) {
    this.crun = crun;
  }
}
