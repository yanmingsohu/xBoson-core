package com.xboson.script;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * require 方法提供的系统模块
 */
public class SysModules implements ISysModuleProvider {

  private Log log = LogFactory.create();
  private Map<String, Class<?>> names;
  private Map<String, Object> instances;


  public SysModules() {
    names = new HashMap<>();
    instances = new HashMap<>();
  }


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


  public void regInstance(String name, Object object) {
    if (name == null)
      throw new NullPointerException("name");
    if (object == null)
      throw new NullPointerException("object");

    instances.put(name, object);
  }


  public void regClass(String name, Class<?> clazz) {
    if (name == null)
      throw new NullPointerException("name");
    if (clazz == null)
      throw new NullPointerException("clazz");

    names.put(name, clazz);
  }


  public void config(Sandbox box) throws ScriptException {
    try {
      box.getGlobalFunc().invokeFunction(
              "__set_sys_module_provider", this);
    } catch(NoSuchMethodException e) {
      throw new ScriptException(e);
    }
  }
}
