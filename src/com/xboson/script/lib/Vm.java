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
// 文件创建日期: 18-1-18 下午12:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/Vm.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.been.XBosonException;
import com.xboson.script.JSObject;
import com.xboson.script.Sandbox;
import com.xboson.script.SandboxFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;

import javax.script.*;
import java.io.StringReader;


public class Vm extends JSObject {

  public static final String VM_CONTEXT = "xBoson.vm.context";


  public Object Script(String code, ScriptObjectMirror options)
          throws ScriptException {
    return new Script(code, options);
  }


  public Object createContext() {
    return createContext(ScriptUtils.wrap(Context.getGlobal().newObject()));
  }


  public Object createContext(ScriptObjectMirror sandbox) {
    ScriptContext context = new SimpleScriptContext();
    sandbox.setMember(VM_CONTEXT, context);
    context.setBindings(sandbox, ScriptContext.GLOBAL_SCOPE);
    return sandbox;
  }


  public boolean isContext(ScriptObjectMirror o) {
    Object r = o.getMember(VM_CONTEXT);
    if (r != null) {
      return r instanceof ScriptContext;
    }
    return false;
  }


  public Object runInContext(String code, ScriptObjectMirror sandbox)
          throws ScriptException {
    return runInContext(code, sandbox, null);
  }


  public Object runInContext(String code,
                             ScriptObjectMirror sandbox,
                             ScriptObjectMirror options)
          throws ScriptException {
    ScriptContext context = (ScriptContext) sandbox.getMember(VM_CONTEXT);
    if (context == null) {
      throw new XBosonException.BadParameter("sandbox", "is not context");
    }
    if (options != null) {
      setOptions(context, options);
    }
    Sandbox box = SandboxFactory.create();
    return box.eval(code, context);
  }


  private void setOptions(ScriptContext context, ScriptObjectMirror options) {
    Object filename = options.get("filename");
    if (filename != null) {
      context.setAttribute(ScriptEngine.FILENAME,
              filename, ScriptContext.GLOBAL_SCOPE);
    }
  }


  public class Script {
    private Sandbox box;
    private CompiledScript script;
    private ScriptObjectMirror options;

    public Script(String code, ScriptObjectMirror options)
            throws ScriptException {
      this.box = SandboxFactory.create();
      this.script = box.compile(new StringReader(code));
      this.options = options;
    }

    public Object runInContext(ScriptObjectMirror sandbox, ScriptObjectMirror opt)
            throws ScriptException {
      ScriptContext context = (ScriptContext) sandbox.getMember(VM_CONTEXT);
      setOptions(context, options);
      return script.eval(context);
    }
  }
}
