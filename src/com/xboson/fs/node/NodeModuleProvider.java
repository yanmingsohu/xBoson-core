////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-22 下午4:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/node/NodeModuleProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.node;

import com.xboson.been.XBosonException;
import com.xboson.fs.redis.IFileSystemConfig;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.ICodeRunner;
import com.xboson.script.IModuleProvider;
import com.xboson.script.Sandbox;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


/**
 * 按照 nodejs 的规则来加载脚本文件.
 */
public class NodeModuleProvider implements IModuleProvider {

  /**
   * node 目录必须有 package.json 文件, 且 name 为该名称.
   */
  public static final String PROJECT_NAME = "xboson-node-modules";
  public static final String NODE_MODULES = "/NODE_MODULES";


  private IRedisFileSystemProvider fs;
  private IModuleProvider parent;
  private String localPath;
  private Log log;


  NodeModuleProvider(IRedisFileSystemProvider fs,
          IFileSystemConfig config, IModuleProvider parent) {
    this.fs = fs;
    this.parent = parent;
    this.localPath = config.configLocalPath();
    this.log = LogFactory.create();
    check();
  }


  private void check() {
    String pkgName = localPath + "/package.json";

    try (FileInputStream fin = new FileInputStream(pkgName)) {
      StringBufferOutputStream buf = new StringBufferOutputStream();
      buf.write(fin, false);
      Map pkgInf = Tool.getAdapter(Map.class).fromJson(buf.toString());

      String name = (String) pkgInf.get("name");
      if (! PROJECT_NAME.equalsIgnoreCase(name)) {
        throw new XBosonException("bad package name: " + name);
      }

      log.debug("Node modules:", pkgInf, "in", pkgName);
      localPath = localPath + NODE_MODULES;
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  @Override
  public Object getModule(String name) {
    Object mod = parent.getModule(name);
    if (mod != null)
      return mod;

    String file = localPath + name;
    Tool.pl("!!!!!!!!!", file);

    return null;
  }


  @Override
  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    parent.config(box, runner);
  }
}
