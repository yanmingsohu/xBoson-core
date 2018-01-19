////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-19 下午3:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestMasquerade.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.Module;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.script.IScriptFileSystem;
import com.xboson.fs.ui.UIFileFactory;
import com.xboson.script.*;
import com.xboson.test.impl.TestServletRequest;
import com.xboson.test.impl.TestServletResponse;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.Writer;


public class TestMasquerade extends Test implements IConfigSandbox {
  private Module masquerade;

  @Override
  public void test() throws Throwable {
    sub("Basic");
    IScriptFileSystem vfs = TestScript.createVFS();
    Application app = TestScript.createBasicApplication(vfs);
    app.config(this);
    page();
  }


  private void page() throws Throwable {
    sub("Request page");
    ScriptObjectMirror init = (ScriptObjectMirror) masquerade.exports;
    IRedisFileSystemProvider uifs = UIFileFactory.open();
    ScriptObjectMirror service =
            (ScriptObjectMirror) init.call(null, "/", true, uifs);

    TestServletResponse resp = new TestServletResponse();
    TestServletRequest req = new TestServletRequest();
    req.requestUri = "/t/index.htm";
    service.call(null, req, resp);
    resp.getWriter().flush();
  }


  public static void main(String[] a) {
    new TestMasquerade();
  }


  @Override
  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    sub("Init");
    String fileName = "./masquerade.js";
    StringBufferOutputStream buf =
        Tool.readFileFromResource(UIFileFactory.class, fileName);
    ScriptContext context = box.createContext();

    WrapJavaScript js = box.warp(fileName, buf.toString());
    js.compile(box);
    js.initModule(runner);

    masquerade = js.getModule();
    msg(fileName, masquerade);
  }
}
