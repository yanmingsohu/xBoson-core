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
// 文件创建日期: 2017年11月3日 上午11:52:58
// 原始文件路径: xBoson/src/com/xboson/script/Sandbox.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.script.*;

import com.xboson.been.XBosonException;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


/**
 * 一个编译好的脚本, 可以反复运行.
 */
public class Sandbox implements IVisitByScript {

  private final static String BOoTjs = "./bootstrap.js";
  private final static String eS6Pf = "./es6-polyfill.js";

  private ScriptEngine js;
  private Bindings bind;
  private ScriptContext context;
  private Compilable cpl;
  private boolean ispred = false;


  Sandbox(ScriptEngine engine) throws ScriptException {
    if (engine instanceof Compilable) {
      cpl = (Compilable) engine;
    } else {
      throw new ScriptException("Cannot compile");
    }

    this.js      = engine;
    this.bind    = js.getBindings(ScriptContext.ENGINE_SCOPE);
    this.context = js.getContext();
  }


  /**
   * 创建一个新的上下文环境, 继承了全局上下文中的对象
   * http://wiki.openjdk.java.net/display/Nashorn/Nashorn+jsr223+engine+notes
   */
  public ScriptContext createContext() {
    ScriptContext new_context = new SimpleScriptContext();
    //
    // 继承全局对象中的属性和方法
    //
    new_context.setBindings(bind, ScriptContext.ENGINE_SCOPE);
    //
    // 设置局部上下文对象
    //
    new_context.setBindings(js.createBindings(), ScriptContext.GLOBAL_SCOPE);
    return new_context;
  }


  /**
   * 冻结对象
   * @param name - 如果为空则冻结全局对象
   * @throws ScriptException
   */
  public void freeze(String name) throws ScriptException {
    if (name == null) {
      throw new ScriptException("parm 'name' is null");
    }
    eval("Object.freeze(" + name + ")");
  }


  /**
   * 冻结全局对象, 即使 hack 也无法修改全局绑定的工具对象,
   * 无法在全局定义变量/函数.
   *
   * !!锁住导致文件名无法与脚本绑定, 抛出的异常没有文件路径
   *
   * @throws ScriptException
   */
  public void freezeGlobal() throws ScriptException {
    freeze("this");
  }


  /**
   * 将代码包装到函数中, 并且使用 nodejs 语法
   */
  public WrapJavaScript warp(String filename, String code) throws ScriptException {
    return new WrapJavaScript(code, filename);
  }


  /**
   * 不指定文件名
   */
  public WrapJavaScript warp(String code) throws ScriptException {
    return new WrapJavaScript(code, "<wrap>");
  }


  /**
   * 用默认上下文执行脚本
   */
  public Object eval(String code) throws ScriptException {
    return eval(code, this.context);
  }


  /**
   * 用指定的上下文执行脚本
   */
  public Object eval(String code, ScriptContext context) throws ScriptException {
    return js.eval(code, context);
  }


  /**
   * 用默认上下文执行脚本
   */
  public Object eval(InputStream instream) throws ScriptException {
    return eval(instream, this.context);
  }


  /**
   * 用指定的上下文执行脚本
   */
  public Object eval(InputStream instream, ScriptContext context)
          throws ScriptException {
    try {
      Reader reader = new InputStreamReader(instream);
      return js.eval(reader, context);
    } finally {
      Tool.close(instream);
    }
  }


  public CompiledScript compile(Reader code) throws ScriptException {
    try {
      return cpl.compile(code);
    } catch(ScriptException e) {
      throw new JScriptException(e, code);
    }
  }


  /**
   * 返回默认全局上下文中的可执行对象, 用于调用默认全局上下文中的函数
   * 该方法不能返回自定义上下文中的可执行对象.
   *
   * @see #invokeFunction
   */
  public Invocable getGlobalInvocable() {
    return (Invocable) js;
  }


  /**
   * 执行当前上下文中的函数
   *
   * @param name 函数名
   * @param params 参数
   * @return js 函数返回值
   * @throws ScriptException 找不到函数或不是函数
   */
  public Object invokeFunction(String name, Object...params)
          throws ScriptException {
    ScriptObjectMirror func = (ScriptObjectMirror) bind.get(name);
    if (func == null)
      throw new ScriptException("Cannot function '" + name + "'");

    if (! func.isFunction())
      throw new XBosonException("Is not a function '" + name + "'");

    return func.call(bind, params);
  }


  public Object bootstrap() throws ScriptException {
    if (ispred) return null;
    ispred = true;
    setFilename("<bootstrap>");
    URL url = Tool.getResource(Sandbox.class, BOoTjs);
    if (url == null) {
      throw new ScriptException("Serious error "+ BOoTjs +" cannot found");
    }
    return eval(url);
  }


  void es6Polyfill() throws ScriptException {
    setFilename(eS6Pf);
    URL url = Tool.getResource(Sandbox.class, eS6Pf);
    eval(url);
  }


  private Object eval(URL url) throws ScriptException {
    try {
      InputStream script1 = url.openStream();
      return eval(script1);
    } catch (IOException e) {
      throw new ScriptException("Load "+ url.getFile()
              +" failed "+ e.getMessage());
    }
  }


  public void bootstrapEnvReady() throws ScriptException {
    invokeFunction("__env_ready");
  }


  public void bootstrapEnd() throws ScriptException {
    invokeFunction("__boot_over");
  }


  /**
   * 在 compile 之前设置有效
   */
  public void setFilename(String name) {
    context.setAttribute(ScriptEngine.FILENAME, name, ScriptContext.ENGINE_SCOPE);
  }


  /**
   * 返回全局上下文中的 binding
   */
  public Bindings getBindings() {
    return bind;
  }


  /**
   * 返回全局上下文
   */
  public ScriptContext getContext() {
    return context;
  }
}
