////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月7日 10:41
// 原始文件路径: xBoson/src/com/xboson/script/EnvironmentFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Console;
import com.xboson.script.lib.Path;
import com.xboson.script.lib.Uuid;

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
    sysmod.regClass("sys/uuid",
            Uuid.class);

    sysmod.loadLib("sys/util",
            "lib/sysutil.js");
    sysmod.loadLib("sys/json",
            "lib/JSON.js");
    sysmod.loadLib("util",
            "lib/util.js");
    sysmod.loadLib("assert",
            "lib/assert.js");
    sysmod.loadLib("events",
            "lib/events.js");
    sysmod.loadLib("buffer",
            "lib/buffer.js");
    sysmod.loadLib("querystring",
            "lib/querystring.js");
    sysmod.loadLib("punycode",
            "lib/punycode.js");
    sysmod.loadLib("url",
            "lib/url.js");
    sysmod.loadLib("uuid",
            "lib/uuid.js");

    BasicEnvironment env = new BasicEnvironment(sysmod);
    env.setEnvObject(Console.class);

    return env;
  }
}
