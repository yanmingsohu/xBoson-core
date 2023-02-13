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
// 文件创建日期: 2017年11月5日 上午9:56:07
// 原始文件路径: xBoson/src/com/xboson/script/WrapJavaScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.XBosonException;
import com.xboson.util.ReaderSet;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.nio.ByteBuffer;


/**
 * 用来包装 js 源代码
 */
public class WrapJavaScript extends AbsWrapScript {

  /**
   * engine 的安全问题:
   * https://github.com/javadelight/delight-nashorn-sandbox/issues/73
   */
  private static final String S_WRAP0 = "__warp_main(function(" +
          "require, module, __dirname , __filename, exports, console, engine) {";
  private static final String S_WRAP1 = "\n})";

  private ReaderSet code_reader;
  private CompiledScript cs;
  private ScriptContext context;


  protected WrapJavaScript(String filename) {
    super(filename);
    this.code_reader = new ReaderSet();
  }


  public WrapJavaScript(byte[] code, String filename) {
    this(ByteBuffer.wrap(code), filename);
  }


  public WrapJavaScript(ByteBuffer code, String filename) {
    this(filename);
    code_reader.add(S_WRAP0);
    code_reader.add(code);
    code_reader.add(S_WRAP1);
  }


  public WrapJavaScript(String code, String filename) {
    this(filename);
    code_reader.add(S_WRAP0);
    code_reader.add(code);
    code_reader.add(S_WRAP1);
  }


  public void compile(Sandbox box) {
    try {
      context = box.createContext();
      box.setFilename(filename);
      cs = box.compile(code_reader);

    } catch (ScriptException e) {
      throw new JScriptException(e, code_reader, filename);
    }
  }


  public Object initModule(ICodeRunner crun) {
    try {
      //
      // jso 是在 'bootstrap.js' 脚本中 __warp_main 函数返回的函数.
      //
      AbstractJSObject jso = (AbstractJSObject) cs.eval(context);
      Object warpreturn = jso.call(module, module, crun);
      module.loaded = true;
      return warpreturn;

    } catch (ECMAException ec) {
      throw new JScriptException(ec, code_reader, filename);

    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }

}
