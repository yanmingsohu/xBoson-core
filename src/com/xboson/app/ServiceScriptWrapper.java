////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.script.*;
import com.xboson.util.CloseableSet;
import com.xboson.util.IConstant;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;


public class ServiceScriptWrapper implements IConstant, IConfigSandbox {

  private static final byte[] warp0 =
          "module.exports = (function(sys, sql, cache, http, se) {"
                  .getBytes(CHARSET);

  private static final byte[] warp1 =
          "});".getBytes(CHARSET);

  private static final Class[] libs = new Class[] {
          MapImpl.class,
          DateImpl.class,
          ListImpl.class,
          ModuleHandleContext.class,
  };

  private static final String[] configuration_script = new String[] {
          "lib/array_sort_comparator.js",
          "lib/sys_functions_impl.js",
          "lib/string_functions.js",
          "lib/inner_call.js",
  };

  private IEnvironment env;


  public ServiceScriptWrapper() throws IOException {
    BasicEnvironment basic = EnvironmentFactory.createBasic();
    basic.setEnvObjectList(libs);
    basic.insertConfiger(this);
    this.env = basic;
  }


  public IEnvironment getEnvironment() {
    return env;
  }


  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    for (int i=0; i<configuration_script.length; ++i) {
      String filepath = configuration_script[i];
      box.setFilename(filepath);
      box.eval(Tool.readFileFromResource(
              getClass(), filepath).openInputStream());
    }
    bindApiConstant(box, IApiConstant.class);
  }


  /**
   * 运行编译好的 api, 应答来自 servlet 的请求;
   * 支持多线程.
   *
   * @param cd 请求数据, 应答对象
   * @param jsmod 编译好的模块
   * @param org 机构
   */
  public void run(CallData cd, Module jsmod, XjOrg org) {
    ScriptObjectMirror call = (ScriptObjectMirror) jsmod.exports;

    if (! call.isFunction() )
      throw new XBosonException("Script wrapper fail.");

    ConnectConfig orgdb = org.getOrgDb();

    try (CloseableSet cs = new CloseableSet()) {
      SqlImpl sql     = cs.add(new SqlImpl(cd, orgdb));
      SysImpl sys     = new SysImpl(cd, orgdb);
      CacheImpl cache = new CacheImpl(cd, org.id());
      HttpImpl http   = new HttpImpl(cd);
      SeImpl se       = null;

      sql._setSysRef(sys);
      ModuleHandleContext.register("sql", sql);

      if (org.isSysORG()) {
        se = cs.add(new SeImpl(cd, sys, org.id()));
      }

      call.call(jsmod.exports, sys, sql, cache, http, se);

    } catch (ECMAException ec) {
      throw new JScriptException(ec);

    } catch (XBosonException x) {
      throw x;

    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 包装来自表的脚本, 使之可以成为 web 服务脚本
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
