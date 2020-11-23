////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午11:01
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/ScriptEnv.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.app.ScriptEnvConfiguration;
import com.xboson.been.Module;
import com.xboson.been.XBosonException;
import com.xboson.fs.node.NodeFileFactory;
import com.xboson.fs.script.IScriptFileSystem;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.script.*;
import com.xboson.util.c0nst.IConstant;
import org.bson.Document;

import java.io.IOException;
import java.nio.ByteBuffer;


public class ScriptEnv implements IScriptFileSystem, IotConst {

  private static final byte[] AdditionCode =
          ("\n;\ntry { module.exports = {" +
            FUNCTION_DATA +":"+ FUNCTION_DATA +","+
            FUNCTION_CMD  +":"+ FUNCTION_CMD +
          "} } catch(e) { console.error(e) }").getBytes(IConstant.CHARSET);

  private Util util;
  private Application app;


  ScriptEnv(Util util) {
    this.util = util;

    try {
      BasicEnvironment basic = EnvironmentFactory.createEmptyBasic();
      SysModules sys_mod = EnvironmentFactory.createDefaultSysModules();
      IConfigurableModuleProvider node_mod =
              NodeFileFactory.openNodeModuleProvider(sys_mod);

      sys_mod.regClasses(ScriptEnvConfiguration.dynamic_library());
      basic.insertConfiger(node_mod);

      this.app = new Application(basic, this);
    } catch(Exception e) {
      throw new XBosonException(e);
    }
  }


  Module run(String id) {
    return app.run(SCRIPT_PATH_P + id);
  }


  void changed(String id) {
    app.changed(SCRIPT_PATH_P + id);
  }


  private Document getScript(String path) throws IOException {
    if (path.startsWith(SCRIPT_PATH_P)) {
      path = path.substring(SCRIPT_PATH_P.length());
    }
    return util.getScript(path);
  }


  @Override
  public ByteBuffer readFile(String path) throws IOException {
    Document sc = getScript(path);
    byte[] b = Util.secr.decryptApi2(sc.getString("code"), sc.getInteger("z"));
    ByteBuffer buff = ByteBuffer.allocate(b.length + AdditionCode.length);
    buff.put(b);
    buff.put(AdditionCode);
    return buff;
  }


  @Override
  public ScriptAttr readAttribute(String path) throws IOException {
    Document sc = getScript(path);
    ScriptAttr attr = new ScriptAttr();

    int sp = path.lastIndexOf('/');
    if (sp > 0) {
      attr.fileName = path.substring(sp+1);
      attr.pathName = path.substring(0, sp);
    } else {
      attr.fileName = path;
      attr.pathName = "";
    }

    attr.fullPath = path;
    attr.createTime = sc.getDate("cd").getTime();
    attr.modifyTime = sc.getDate("md").getTime();
    attr.fileSize = sc.getString("code").length();
    return attr;
  }


  @Override
  public String getID() {
    return "iot";
  }


  @Override
  public String getType() {
    return "Iot-script-fs";
  }
}
