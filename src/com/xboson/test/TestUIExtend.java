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
// 文件创建日期: 21-5-15 下午4:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestUIExtend.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.Module;
import com.xboson.fs.node.NodeFileFactory;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.script.IScriptFileSystem;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.fs.ui.UIFileFactory;
import com.xboson.j2ee.ui.UIExtRenderService;
import com.xboson.script.*;
import com.xboson.util.ReaderSet;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.AbstractJSObject;

import javax.script.ScriptException;
import java.io.*;
import java.nio.ByteBuffer;


/**
 * UI 扩展脚本测试失败: 打包失败, 运行失败, js引擎不能正确解析脚本.
 * jdk8 nashorn 解析失败
 */
public class TestUIExtend extends Test implements IConfigSandbox,
        IScriptFileSystem, UIExtRenderService.IRenderFile, UIExtRenderService.IFileReader {

  private static final String S_WRAP0 = "__warp_main(function(" +
          "require, module, __dirname , __filename, exports, console, engine) {";
  private static final String S_WRAP1 = "\n})";

  private static final String fileName = "xboson-ui-ext.pack.js";
  private Module uiext;


  public void test() throws Exception {
//    write_packd_code_to_disk();
//    readscript();
//    read_script_direct();
//    test_render_server();
  }


  // 本地文件绑定, 测试可能失败
  private void test_render_server() throws Exception {
    sub("Test render server");
    UIExtRenderService rs = new UIExtRenderService(this);

    String full = "D:\\javaee-project\\xboson-ui-ext\\test\\app.vue";

    // 等待初始化
    Tool.sleep(1000);
    rs.render(full, readfile(full), this, null);
    Tool.sleep(5000);
    msg("ok");
  }


  @Override
  public void render(byte[] content, String mime, String filepath) {
    msg("Render callback", filepath, mime, new String(content));
  }


  @Override
  public void error(String message) {
    msg("Render error", message);
  }


  @Override
  public void startAsync() {
  }


  @Override
  public byte[] readfile(String fullpath) throws IOException {
    StringBufferOutputStream i = new StringBufferOutputStream(1000);
    i.write(new FileInputStream(fullpath));
    return i.toBytes();
  }


  private void write_packd_code_to_disk() throws Exception {
    String outfile = "/down1/ui-pack.wrap.js";
    sub("Write javascript to", outfile);
    ReaderSet rs = new ReaderSet();
    rs.add(S_WRAP0);
    rs.add(readCode());
    rs.add(S_WRAP1);

    FileOutputStream out = new FileOutputStream(outfile);
    OutputStreamWriter wout = new OutputStreamWriter(out);
    Tool.copy(rs, wout, true);
  }


  /**
   * 不对代码做任何处理, 编译后如果出现问题, 则执行该测试
   * (失败, 无法解析打包后的文件)
   * @throws Exception
   */
  private void read_script_direct() throws Exception {
    sub("Load main script direct(not boot)", fileName);
    Sandbox a = SandboxFactory.create();
    AbstractJSObject func = (AbstractJSObject) a.eval(";(function(require, module) {"+
            readCode() +
            "});" );
    func.call(null, null, null);
  }


  /**
   * 完整的模块脚本加载
   * (失败, 无法解析打包后的文件)
   * @throws Exception
   */
  private void readscript() throws Exception {
    sub("Load main script", fileName);
    msg("Cannot support ui extends javascript");
    SysModules sysmod = EnvironmentFactory.createDefaultSysModules();

    IConfigurableModuleProvider nodejs_mod =
            NodeFileFactory.openNodeModuleProvider(sysmod);

    BasicEnvironment env = EnvironmentFactory.createEmptyBasic();
    env.insertConfiger(nodejs_mod);

    Application app = new Application(env, this);
    app.config(this);
//    app.run(fileName);
  }


  public static void main(String[] a) {
    new TestUIExtend();
  }


  @Override
  public ByteBuffer readFile(String path) throws IOException {
    if (fileName.equals(path)) {
      StringBufferOutputStream buf =
              Tool.readFileFromResource(UIFileFactory.class, fileName);
      return buf.toBuffer();
    }
    return null;
  }


  @Override
  public ScriptAttr readAttribute(String path) throws IOException {
    return null;
  }


  @Override
  public String getID() {
    return null;
  }


  @Override
  public String getType() {
    return null;
  }


  @Override
  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    WrapJavaScript js = box.warp(fileName, readCode());
    js.compile(box);
    js.initModule(runner);

    uiext = js.getModule();
  }


  private String readCode() {
    StringBufferOutputStream buf =
            Tool.readFileFromResource(UIFileFactory.class, fileName);
    return buf.toString();
  }
}
