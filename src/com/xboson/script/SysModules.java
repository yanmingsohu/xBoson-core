package com.xboson.script;

import com.xboson.been.Module;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.StringBufferOutputStream;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * require 方法提供的系统模块
 */
public class SysModules implements ISysModuleProvider {

  private Log log = LogFactory.create();
  private Map<String, Class<?>> names;
  private Map<String, Object> instances;
  private Map<String, String> jscode;


  public SysModules() {
    names = new HashMap<>();
    instances = new HashMap<>();
    jscode = new HashMap<>();
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
    InputStream r = getClass().getResourceAsStream(jsfile);
    StringBufferOutputStream buf = new StringBufferOutputStream();
    buf.write(r);
    jscode.put(name, buf.toString());
  }


  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    try {
      box.getGlobalFunc().invokeFunction(
              "__set_sys_module_provider", this);

      Iterator<String> it = jscode.keySet().iterator();
      while (it.hasNext()) {
        String name = it.next();

        box.setFilename("<" + name + ">");
        WarpdScript ws = new WarpdScript(box, jscode.get(name));
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
