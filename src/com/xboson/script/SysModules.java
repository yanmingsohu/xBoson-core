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
// 文件创建日期: 2017年11月11日 10:31
// 原始文件路径: xBoson/src/com/xboson/script/SysModules.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.been.Module;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * require 方法提供的系统模块, 需要在初始化时将外部模块注入.
 * 每个环境只支持一个 SysModules 模块, 切只能绑定一个运行时.
 */
public class SysModules extends AbsModules implements IModuleProvider {

  private Log log = LogFactory.create();
  private ICodeRunner runner;

  /** 未实例化的 java 对象类型的 js 模块 */
  private Map<String, AbsWrapScript> modules;


  public SysModules() {
    modules = new ConcurrentHashMap<>();
  }


  /**
   * 返回一个本地模块
   */
  public Module getModule(String name, Module apply) {
    if (name == null)
      throw new NullPointerException("name");

    AbsWrapScript module = modules.get(name);
    if (module == null)
      return null;

    Module mod = runner.run(module);
    mod.loaderid = LOADER_ID_SYS_MODULE;
    modules.remove(name);
    return mod;
  }


  /**
   * 注册模块类, 在需要时创建类的实例
   */
  public void regClass(String name, Class<?> clazz) {
    if (name == null)
      throw new NullPointerException("name");
    if (clazz == null)
      throw new NullPointerException("clazz");

    modules.put(name, new NativeWrapScript(name, clazz));
  }


  /**
   * 注册集合中的模块类, 在需要时创建类的实例
   */
  public void regClasses(Map<String, Class> map) {
    for (Map.Entry<String, Class> en : map.entrySet()) {
      regClass(en.getKey(), en.getValue());
    }
  }


  /**
   * 注册 java 模块, 模块已经实例化
   */
  public void regLib(String name, Object lib) {
    modules.put(name, new NativeWrapScript(name, lib));
  }


  /**
   * 使用 java 类作为文件资源加载器, 读取并解析 js 代码, 并装载到系统模块
   */
  public LibLoader open(Class loader) {
    return new LibLoader(loader);
  }


  /**
   * 调用 'bootstrap.js' 中的 __set_sys_module_provider 方法来初始化环境.
   */
  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    box.invokeFunction("__set_sys_module_provider", this);
    this.runner = runner;
  }


  /**
   * js 类加载器, 基于一个 java 的类加载器作为文件加载器
   */
  public class LibLoader {

    private Class fileLoader;

    private LibLoader(Class _loader) {
      this.fileLoader = _loader;
    }

    /**
     * 读取 js 脚本, 并作为系统模块
     * @param name - 模块名称
     * @param jsfile - js 文件路径, 相对于 SysModules 类
     */
    public void load(String name, String jsfile) {
      StringBufferOutputStream buf =
              Tool.readFileFromResource(fileLoader, jsfile);
      modules.put(name, new WrapJavaScript(buf.toBuffer(), name));
    }
  }
}
