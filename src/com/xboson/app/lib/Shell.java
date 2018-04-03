////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-15 下午1:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/Shell.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.ApiAuthorizationRating;
import com.xboson.been.XBosonException;
import com.xboson.db.IDict;
import com.xboson.event.timer.TimeFactory;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.lib.Path;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.SysConfig;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;


public class Shell extends RuntimeUnitImpl implements IAResource {

  public final static int MAX_RUN_TIME = 30 * 60 * 1000;

  private final String basePath;
  private final boolean isWindows;
  private final Log log;


  public Shell() {
    super(null);
    basePath = SysConfig.me().readConfig().shellUrl + '/';
    isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    log = LogFactory.create();
  }


  public Inner open() throws Exception {
    PermissionSystem.applyWithApp(ApiAuthorizationRating.class, this);
    boolean runOnSysOrg = (boolean) ModuleHandleContext._get("runOnSysOrg");

    if (!runOnSysOrg) {
      throw new XBosonException.NotImplements("只能在平台机构中引用");
    }

    if (!AppContext.me().who().isRoot()) {
      SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");

      if (IDict.ADMIN_FLAG_ADMIN.equals(sys.getUserAdminFlag())) {
        throw new XBosonException.NotImplements("只有平台管理员可以调用");
      }
    }
    return new Inner();
  }


  @Override
  public String description() {
    return "app.module.shell.functions()";
  }


  public class Inner {
    private Inner() {}

    public Object execute(String fileName) throws IOException {
      fileName = Path.me.normalize(fileName);

      if (! fileName.contains(".")) {
        if (isWindows) {
          fileName += ".cmd";
        } else {
          fileName += ".sh";
        }
      }

      ProcessBuilder build = new ProcessBuilder(basePath + fileName);
      build.redirectErrorStream(true);
      Process process = build.start();
      Stoper stop = new Stoper(process);
      TimeFactory.me().schedule(stop, MAX_RUN_TIME);

      ScriptObjectMirror ret = createJSObject();
      ret.setMember("output", toString(process.getInputStream()));
      ret.setMember("code",   process.exitValue());
      ret.setMember("path",   basePath + fileName);
      stop.cancel();
      return ret;
    }


    private String toString(InputStream in) throws IOException {
      StringBufferOutputStream buf = new StringBufferOutputStream();
      buf.write(in);
      return buf.toString();
    }
  }


  private class Stoper extends TimerTask {
    private Process p;

    private Stoper(Process p) {
      this.p = p;
    }

    @Override
    public void run() {
      try {
        cancel();
        p.destroy();
        log.debug("Destory Timeout process", p);
      } catch (Exception e) {
        log.error("Destroy Shell process", p, e);
      }
    }
  }
}
