////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * require 方法提供的系统模块,
 * 每个环境只支持一个 SysModules 模块.
 */
public class SysModules implements ISysModuleProvider {

  private Log log = LogFactory.create();
  private Map<String, Class<?>> names;
  private Map<String, Object> instances;
  private Map<String, String> jscode;


  public SysModules() {
    names = new HashMap<>();
    instances = new HashMap<>();
    // 严格按照顺序加载
    jscode = new LinkedHashMap<>();
  }


  /**
   * 返回一个本地模块
   */
  public Object getInstance(String name) {
    if (name == null)
      throw new NullPointerException("name");

    Object o = instances.get(name);
    if (o == null) {
      Class<?> c = names.get(name);
      if (c == null)
        return null;

      try {
        o = c.newInstance();
        instances.put(name, o);
      } catch(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return null;
      }
    }
    return o;
  }


  /**
   * 注册模块的实例, 在需要时返回该实例
   */
  public void regInstance(String name, Object object) {
    if (name == null)
      throw new NullPointerException("name");
    if (object == null)
      throw new NullPointerException("object");

    instances.put(name, object);
  }


  /**
   * 注册模块类, 在需要时创建类的实例
   */
  public void regClass(String name, Class<?> clazz) {
    if (name == null)
      throw new NullPointerException("name");
    if (clazz == null)
      throw new NullPointerException("clazz");

    names.put(name, clazz);
  }


  /**
   * 读取 js 脚本, 并作为系统模块
   * @param name - 模块名称
   * @param jsfile - js 文件路径, 相对于 SysModules 类
   */
  public void loadLib(String name, String jsfile) throws IOException {
    StringBufferOutputStream buf = Tool.readFileFromResource(getClass(), jsfile);
    jscode.put(name, buf.toString());
  }


  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    try {
      box.getGlobalFunc().invokeFunction(
              "__set_sys_module_provider", this);

      Iterator<String> it = jscode.keySet().iterator();
      while (it.hasNext()) {
        String name = it.next();

        String filename = "<" + name + ">";
        WarpdScript ws = new WarpdScript(box, jscode.get(name), filename);
        ws.setCodeRunner(runner);
        ws.call();

        Module mod = ws.getModule();
        regInstance(name, mod.exports);
      }
    } catch(NoSuchMethodException e) {
      throw new ScriptException(e);
    }
  }
}
