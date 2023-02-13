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
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.db.IDict;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.IPing;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.script.lib.Path;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.internal.runtime.Version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Shell extends RuntimeUnitImpl implements IAResource {

  public static final String RPC_NAME = "XB.rpc.OS.Shell";
  public final static int MAX_RUN_TIME = 30 * 60;

  private final String basePath;
  private final boolean isWindows;
  private final Log log;


  public Shell() {
    super(null);
    basePath = SysConfig.me().readConfig().shellUrl;
    isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    log = LogFactory.create("script.lib.shell");

    try {
      RpcFactory rpc = RpcFactory.me();
      if (! rpc.isBind(RPC_NAME)) {
        rpc.bind(new ShellImpl(), RPC_NAME);
      }
    } catch (Exception e) {
      log.error(e);
    }
  }


  public IShell open() throws Exception {
    return open(IConstant.DEFAULT_NODE_ID);
  }


  public IShell open(String nodeID) throws Exception {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    boolean runOnSysOrg = (boolean) ModuleHandleContext._get("runOnSysOrg");

    if (!runOnSysOrg) {
      throw new XBosonException.NotImplements("只能在平台机构中引用");
    }

    if (!AppContext.me().who().isRoot()) {
      SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");

      Object admin_flag = sys.getUserAdminFlag();
      if ((! IDict.ADMIN_FLAG_ADMIN.equals(admin_flag)) &&
          (! IDict.ADMIN_FLAG_TENANT_ADMIN.equals(admin_flag)) ) {
        throw new XBosonException.NotImplements(
                "只有平台/机构管理员可以调用");
      }
    }

    RpcFactory rpc = RpcFactory.me();
    return (IShell) rpc.lookup(nodeID, RPC_NAME);
  }


  public interface IShell extends IXRemote {
    Object execute(String fileName)  throws IOException;
    Object execute(String fileName, String[] args) throws IOException;
    Object execute(String fileName, String pwd, String[] args) throws IOException;
    void putEnv(String name, String val) throws RemoteException;
    String getEnv(String name) throws RemoteException;
    void clearEnv() throws RemoteException;
  }


  private class ShellImpl implements IShell, IPing {
    private ThreadLocal<Map<String, String>> envVar;


    private ShellImpl() {
      envVar = new ThreadLocal<>();
    }


    private void defaultEnv(Map<String, String> env) {
      Config c = SysConfig.me().readConfig();
      env.put("UI_URL",             c.uiUrl);
      env.put("VERSION",            Version.version());
      env.put("HTTP_PORT",          c.appSelf.httpPort +"");
      env.put("NODE_URL",           c.nodeUrl);
      env.put("SHELL_URL",          c.shellUrl);
      env.put("CLUSTER_NODE_ID",    c.clusterNodeID +"");
      env.put("CONFIG_FILE",        c.configFile);
    }


    @Override
    public Object execute(String fileName)  throws IOException {
      return execute(fileName, null, null);
    }


    @Override
    public Object execute(String fileName, String[] args) throws IOException {
      return execute(fileName, null, args);
    }


    @Override
    public Object execute(String fileName, String pwd, String[] args) throws IOException {
      long begin = System.currentTimeMillis();
      fileName = Path.me.normalize(fileName);
      File fullPath = findExeFile(fileName);

      List<String> command = new ArrayList<>();
      if (fullPath != null) {
        command.add(fullPath.getPath());
      } else {
        command.add(fileName);
      }

      if (args != null && args.length > 0) {
        for (int i=0; i<args.length; ++i) {
          command.add(args[i] +"");
        }
      }

      //
      // ProcessBuilder 内部也将数组转换为 List
      //
      ProcessBuilder build = new ProcessBuilder(command);
      if (pwd != null) {
        build.directory(new File(pwd));
      } else if (fullPath != null) {
        build.directory(new File(fullPath.getParent()));
      } else {
        build.directory(new File(basePath));
      }

      build.redirectErrorStream(true);
      Map<String, String> env = envVar.get();
      if (env != null) {
        build.environment().putAll(env);
      }
      defaultEnv(build.environment());

      Process process = build.start();
      try {
        process.waitFor(MAX_RUN_TIME, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.warn("Shell", e);
      } finally {
        if (process.isAlive()) {
          process.destroy();
        }
      }

      Map<String, Object> ret = new HashMap<>();
      ret.put("output", toString(process.getInputStream()));
      ret.put("code",   process.exitValue());
      ret.put("path",   build.directory().getPath());
      ret.put("elapsed",System.currentTimeMillis() - begin);
      return ret;
    }


    @Override
    public void putEnv(String name, String val) throws RemoteException {
      env().put(name, val);
    }


    @Override
    public String getEnv(String name) throws RemoteException {
      return env().get(name);
    }


    public void clearEnv() throws RemoteException {
      env().clear();
    }


    private Map<String, String> env() {
      Map<String, String> env = envVar.get();
      if (env == null) {
        env = new HashMap<>();
        envVar.set(env);
      }
      return env;
    }


    private String toString(InputStream in) throws IOException {
      StringBufferOutputStream buf = new StringBufferOutputStream();
      buf.write(in);
      return buf.toString();
    }


    private File findExeFile(String fileName) throws IOException {
      if (! fileName.contains(".")) {
        File full = new File(basePath, fileName);
        if (! full.exists()) {
          if (isWindows) {
            full = new File(basePath, fileName + ".cmd");
            if (! full.exists())
              full = new File(basePath, fileName + ".bat");
            if (! full.exists())
              full = new File(basePath, fileName + ".exe");
          } else {
            full = new File(basePath, fileName + ".sh");
          }

          if (! full.exists()) {
            // 抛出异常, 限制命令必须在指定目录中
            // throw new IOException("not found "+ fileName);
            return null;
          }
        }
        return full;
      }
      return null;
    }
  }


  @Override
  public String description() {
    return "app.module.shell.functions()";
  }
}
