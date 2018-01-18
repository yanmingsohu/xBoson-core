////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-18 下午12:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/Vm.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.script.JSObject;
import com.xboson.script.Sandbox;
import com.xboson.script.SandboxFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Context;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;


public class Vm extends JSObject {

  public Object createContext() {
    return new InnerContext(null);
  }


  public Object createContext(ScriptObjectMirror sandbox) {
    return new InnerContext(sandbox);
  }


  public boolean isContext(Object o) {
    return o instanceof InnerContext;
  }


  public Object runInContext(String code, InnerContext context)
          throws ScriptException {
    Sandbox box = SandboxFactory.create();
    return box.eval(code, context.context);
  }


  public final class InnerContext {
    private ScriptContext context;
    private ScriptObjectMirror bind;


    private InnerContext(ScriptObjectMirror bind) {
      context = new SimpleScriptContext();
      if (bind == null) {
        bind = (ScriptObjectMirror) ScriptObjectMirror.wrap(
                Context.getGlobal().newObject(), Context.getGlobal());
      }
      this.bind = bind;
      this.context.setBindings(bind, ScriptContext.GLOBAL_SCOPE);
    }
  }
}
