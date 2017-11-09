package com.xboson.script;

import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Console;
import com.xboson.script.lib.Path;

import java.io.IOException;


public class EnvironmentFactory {

  private EnvironmentFactory() {}


  public static IEnvironment createBasic() throws IOException {
    SysModules sysmod = new SysModules();

    sysmod.regClass("console",
            Console.class);
    sysmod.regClass("path",
            Path.class);
    sysmod.regClass("sys/buffer",
            Buffer.class);

    sysmod.loadLib("util",
            "lib/util.js");
    sysmod.loadLib("assert",
            "lib/assert.js");
    sysmod.loadLib("events",
            "lib/events.js");
    sysmod.loadLib("buffer",
            "lib/buffer.js");

    //    sysmod.loadLib("punycode",    "lib/punycode.js");
    //    sysmod.loadLib("url",         "lib/url.js");
    //    sysmod.loadLib("querystring", "lib/querystring.js");

    BasicEnvironment env = new BasicEnvironment(sysmod);
    env.setEnvObject(Console.class);

    return env;
  }
}
