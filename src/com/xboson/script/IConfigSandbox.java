package com.xboson.script;

import javax.script.ScriptException;

/**
 * 沙箱配置器
 */
public interface IConfigSandbox {

  /**
   * 配置沙箱
   */
  public void config(Sandbox box, ICodeRunner runner) throws ScriptException;

}
