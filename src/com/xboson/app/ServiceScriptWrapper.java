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
// 文件创建日期: 17-11-23 上午10:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ServiceScriptWrapper.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.lib.*;
import com.xboson.been.CallData;
import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.db.ConnectConfig;
import com.xboson.fs.node.NodeFileFactory;
import com.xboson.script.*;
import com.xboson.util.CloseableSet;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


public final class ServiceScriptWrapper implements IConstant, IConfigSandbox {

  private static final byte[] warp0 =
          "module.exports = (function(sys, sql, cache, http, se) {"
                  .getBytes(CHARSET);

  private static final byte[] warp1 =
          "\n});".getBytes(CHARSET);


  /** configuration_script 脚本返回的函数, 名成 : 函数 */
  private Map<String, ScriptObjectMirror> config_return_func;
  private IEnvironment env;


  public ServiceScriptWrapper() throws IOException {
    this.config_return_func = new HashMap<>();
    BasicEnvironment basic = EnvironmentFactory.createEmptyBasic();
    SysModules sys_mod = EnvironmentFactory.createDefaultSysModules();
    IConfigurableModuleProvider node_mod =
            NodeFileFactory.openNodeModuleProvider(sys_mod);

    sys_mod.regClasses(ScriptEnvConfiguration.dynamic_library());

    basic.setEnvObjectList(ScriptEnvConfiguration.global_library());
    basic.insertConfiger(node_mod);
    basic.insertConfiger(this);
    this.env = basic;
  }


  public IEnvironment getEnvironment() {
    return env;
  }


  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    for (String filepath : ScriptEnvConfiguration.environment_script()) {
      box.setFilename(filepath);
      Object ret = box.eval(Tool.readFileFromResource(
              SysImpl.class, filepath).openInputStream());

      if (ret instanceof ScriptObjectMirror) {
        ScriptObjectMirror o = (ScriptObjectMirror) ret;
        if (o.isFunction()) {
          String name = String.valueOf(o.getMember("name"));
          config_return_func.put(name, o);
        }
      }
    }
    bindApiConstant(box, IApiConstant.class);
    runner.addScriptEventListener(AppContext.me());
  }


  /**
   * 运行编译好的 api, 应答来自 servlet 的请求;
   * 支持多线程.
   *
   * @param cd 请求数据, 应答对象
   * @param jsmod 编译好的模块
   * @param org 机构
   * @param api 持有源代码
   */
  public void run(CallData cd, Module jsmod, XjOrg org, XjApp app, XjApi api) {
    ScriptObjectMirror call = (ScriptObjectMirror) jsmod.exports;

    if (! call.isFunction() )
      throw new XBosonException("Is not WEB Service Script (Maybe a library).");

    ConnectConfig orgdb = org.getOrgDb();
    boolean runOnSysOrg = org.isSysORG();

    try (CloseableSet cs = new CloseableSet()) {
      //
      // 乱出新天地
      //
      ConnectionState st = cs.add( new ConnectionState(cd, orgdb) );
      SqlImpl sql        = new SqlImpl(cd, orgdb, st);
      SysImpl sys        = new SysImpl(cd, orgdb, app);
      CacheImpl cache    = new CacheImpl(cd, org.id());
      HttpImpl http      = new HttpImpl(cd);
      SeImpl se          = (runOnSysOrg == false) ? null
                         : new SeImpl(cd, sys, org, st);

      sql._setSysRef(sys);
      ModuleHandleContext.register("sql", sql);
      ModuleHandleContext.register("se",  se);
      ModuleHandleContext.register("sys", sys);
      ModuleHandleContext.register("runOnSysOrg", runOnSysOrg);
      ModuleHandleContext.register(ModuleHandleContext.CLOSE, cs);

      config_return_func.get("__init_system_modules").call(null, sys);

      AppContext.me().readyForKill();
      call.call(jsmod.exports, sys, sql, cache, http, se);

    } catch (ECMAException ec) {
      throw new JScriptException(ec, api.getCode(), api.getApiAttr().fileName);

    } catch (Throwable e) {
      throw new JScriptException(e, api.getCode(), api.getApiAttr().fileName);
    }
  }


  /**
   * 包装脚本, 使之可以成为 web 服务脚本
   * @param code 需要包装的脚本字节缓冲
   * @return
   */
  public ByteBuffer wrap(byte[] code) {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    try {
      buf.write(warp0);
      buf.write(code);
      buf.write(warp1);
      return ByteBuffer.wrap( buf.toByteArray() );
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  private void bindApiConstant(Sandbox box, Class constants) {
    Field[] fs = constants.getFields();
    Bindings bind = box.getBindings();

    for (int i=0; i<fs.length; ++i) {
      try {
        Field f = fs[i];
        bind.put(f.getName(), f.get(null));
      } catch (IllegalAccessException e) {
        throw new XBosonException(e);
      }
    }
  }
}
