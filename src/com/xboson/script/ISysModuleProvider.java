package com.xboson.script;

/**
 * 系统模块提供
 */
public interface ISysModuleProvider extends IConfigSandbox {

  /**
   * 从系统模块名返回系统模块
   * @param name
   * @return 如果找不到模块返回 null
   */
  public Object getInstance(String name);

}
