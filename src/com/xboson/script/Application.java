////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月5日 下午2:43:58
// 原始文件路径: xBoson/src/com/xboson/script/Application.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.script.ScriptException;

import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.fs.IVirtualFileSystem;
import com.xboson.fs.node.NodeModuleProvider;
import com.xboson.util.Tool;


/**
 * 应用存储在二级目录中, 第一级是模块名, 第二级是接口名
 * 不直接支持多线程.
 */
public class Application implements ICodeRunner {

  private Sandbox sandbox;
  private IVirtualFileSystem vfs;
  private Map<String, AbsWrapScript> module_cache;


  public Application(IEnvironment env, IVirtualFileSystem vfs)
          throws ScriptException
  {
    this.vfs = vfs;
    module_cache = Collections.synchronizedMap(new HashMap<>());
    sandbox = SandboxFactory.create();

    sandbox.bootstrap();
    env.config(sandbox, this);
    sandbox.bootstrapEnvReady();
    sandbox.bootstrapEnd();
  }


  /**
   * 使用配置器配置当前的沙箱, 沙箱已经初始化并配置为锁定状态
   * @param configurator
   * @throws ScriptException
   */
  public void config(IConfigSandbox configurator) throws ScriptException {
    configurator.config(sandbox, this);
  }


  /**
   * 运行路径上的脚本, 返回脚本的运行结果.
   * path 参数同时也是缓存 Module 时使用的主键.
   */
  public Module run(String path) {
    AbsWrapScript ws = module_cache.get(path);
    if (ws != null) {
      return ws.getModule();
    }

    try {
      ByteBuffer buf = vfs.readFile(path);
      if (buf == null) {
        throw new XBosonException.IOError(
                "Cannot found Module: "+ path +", null code");
      }

      ws = new WrapJavaScript(buf, path);
      Module mod = run(ws);
      mod.loaderid = IModuleProvider.LOADER_ID_APPLICATION;
      return mod;

    } catch (IOException e) {
      throw new XBosonException.IOError(
              "Cannot found Module: "+ path +", "+ e);
    }
  }


  /**
   * 运行已经初始化了的脚本对象, 该对象尚未编译.
   */
  public Module run(AbsWrapScript ws) {
    String path = ws.getFilename();

    ws.compile(sandbox);
    module_cache.put(path, ws);

    Module mod    = ws.getModule();
    mod.filename  = path;
    mod.id        = '/'+ vfs.getID() +'/'+ path;
    mod.paths     = makePaths(path);
    ws.initModule(this);

    return mod;
  }


  public boolean isCached(String name) {
    return module_cache.containsKey(name);
  }


  private String[] makePaths(String path_name) {
    List<String> paths = new ArrayList<>();
    int i = path_name.lastIndexOf("/", path_name.length());

    while (i >= 0) {
      paths.add(path_name.substring(0, i) + NodeModuleProvider.NODE_MODULES);
      i = path_name.lastIndexOf("/", i-1);
    }
    return paths.toArray(new String[paths.size()]);
  }


  /**
   * 删除路径对应的 js 模块, 这会引起脚本重编译.
   */
  public synchronized void changed(String path) {
    AbsWrapScript ws = module_cache.get(path);
    if (ws != null) {
      ws.getModule().loaded = false;
      module_cache.remove(path);
    }
  }
}
