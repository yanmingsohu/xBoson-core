package com.xboson.script;

import com.xboson.script.lib.Console;
import com.xboson.script.lib.Path;

import java.io.IOException;


public class EnvironmentFactory {

  private EnvironmentFactory() {}


  public static IEnvironment createBasic() throws IOException {
    SysModules sysmod = new SysModules();
    sysmod.regClass("console",  Console.class);
    sysmod.regClass("path",     Path.class);
    sysmod.loadLib("assert",    "lib/assert.js");

    BasicEnvironment env = new BasicEnvironment(sysmod);
    env.setEnvObject(Console.class);

    return env;
  }
}
