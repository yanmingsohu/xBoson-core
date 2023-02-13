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
