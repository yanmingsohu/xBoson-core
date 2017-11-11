////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 上午10:43:25
// 原始文件路径: xBoson/src/com/xboson/test/TestScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.xboson.been.Module;
import com.xboson.log.LogFactory;
import com.xboson.script.*;
import com.xboson.util.StringBufferOutputStream;

/**
 * https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions
 */
public class TestScript extends Test {

	
	public TestScript() throws ScriptException {
	}
	

	public void test() throws Exception {
//		independent_sandbox();
//		speedtest();
//		memoryuse();
//		closed();
		hack();
    fullTest();
	}


  /**
   * 综合测试沙箱的功能和安全性
   * @throws Exception - 测试失败抛出异常
   */
	public void fullTest() throws Exception {
    IEnvironment env = EnvironmentFactory.createBasic();

		FixFile vfs = new FixFile();
		
		Application app = new Application(env, vfs);
		app.run("/index.js");
		success("Full Test");
	}
	
	
	/**
	 * 将测试目录文件映射到虚拟目录
	 */
	static public class FixFile implements IVirtualFileSystem {
		/** <virtual_filename, code> */
		Map<String, String> map = new HashMap<String, String>();
		public FixFile() {}
		
		void putcode(String name, String code) {
			map.put(name, code);
		}
		
		@SuppressWarnings("resource")
		void putfile(String virautlname, String codefile) throws IOException {
			InputStream is = getClass().getResourceAsStream(codefile);
			StringBufferOutputStream sbos = new StringBufferOutputStream();
			sbos.write(is);
			map.put(virautlname, sbos.toString());
		}

		@Override
		public String readFile(String path) throws IOException {
			String code = map.get(path);
			if (code == null) {
        InputStream is = getClass().getResourceAsStream("./js/" + path);
        if (is != null) {
          StringBufferOutputStream sbos = new StringBufferOutputStream();
          sbos.write(is);
          code = sbos.toString();
          map.put(path, code);
        }
      }
			if (code == null) {
        throw new FileNotFoundException("not found " + path);
      }
      return code;
		}

		@Override
		public String getID() {
			return "FIX";
		}
	}
	

	/** 安全检查, 已经移动到 js/check-safe.js */
	public void hack() throws Exception {
		Sandbox sandbox = SandboxFactory.create();
		sandbox.bootstrap();
    IEnvironment env = EnvironmentFactory.createBasic();
		env.config(sandbox, null);
		sandbox.freezeGlobal();
		
		Object o = null;
		try {
      sandbox.warp("console = null; console.log=null; console.a='bad', console.log('hello', console.a)").call();
      sandbox.warp("if (console.a) throw new Error('console.a is changed.')");
      success("cannot modify java object");
    } catch(Exception e) {}
		
		sandbox.warp("try { test.notcall() } catch(e) { test.log(e.stack); }; }), a=19, (function cc(){ return cc").call();
		success("hack the ");
		
		sandbox.warp("if (typeof a !='undefined') throw new Error('bad');").call();
		success("cannot make THIS value");
		
		sandbox.warp("global.a = 11").call();
		sandbox.warp("if (global.a != 11) throw new Error('bad global.a')").call();
		success("can change global val");
		
		o = sandbox.warp("return Math.abs(-1)").call();
		success("Math is done " + o);
		
		try {
			sandbox.warp("setTimeout(function() { console.log('setTimeout !!') }, 1000)").call();
			fail("be not has setTimeout function");
		} catch(Exception e) {
			success("setTimeout done");
		}
		
		WarpdScript ws = sandbox.warp("console.log(JSON.stringify({a:1}), module, __dirname, __filename)");
		Module m = ws.getModule();
		m.filename = "/test/hello.js";
		ws.call();
		success("module ok, exports: "+ m.exports.getClass());
	}
	
	
	@SuppressWarnings("unused")
	private void notcall() {
		throw new RuntimeException();
	}
	
	
	public void closed() throws ScriptException {
		Sandbox a = SandboxFactory.create();
		Object o = a.eval("(function() { return 1; })");
		msg(o + " " + o.getClass());
		success("closed");
	}
	
	
	/**
	 * 实例数量...内存 MB...运行后内存
	 *   100      26       45
	 *  1000     224      331
	 * 10000    2138     2277
	 */
	public void memoryuse() throws ScriptException {
		memuse();
		Sandbox[] a = new Sandbox[10000];
		for (int i=0; i<a.length; ++i) {
			a[i] = SandboxFactory.create();
		}
		memuse();
		for (int i=0; i<a.length; ++i) {
			a[i].eval("a=1");
		}
		memuse();
	}
	
	
	public void independent_sandbox() throws Exception {
		Sandbox a = SandboxFactory.create();
		Sandbox b = SandboxFactory.create();
		String c = "function g() { return a; }";
		a.eval(c);
		b.eval(c);
		a.eval("a = 1;");
		b.eval("a = 2;");
		Integer aa = (Integer) a.getGlobalFunc().invokeFunction("g");
		Integer bb = (Integer) b.getGlobalFunc().invokeFunction("g");
		if (aa == 1 && bb == 2) {
			success("independent sandbox");
		} else {
			throw new Exception("sandbox value cross");
		}
	}

  /**
   * 1 秒内调用函数次数, Math.sqrt()
   * NODE js :  9099763.1
   * java js : 36106378.8
   *
   * 1 秒内调用函数次数, Math.sin(Math.random());
   * NODE js :  8331048.8
   * java js : 16194020.9
   *
   * @throws ScriptException
   */
	public void speedtest() throws ScriptException {
		InputStream script1 = getClass().getResourceAsStream("./test-speed.js");
		Sandbox s = SandboxFactory.create();
		s.getBindings().put("console", this);
		s.eval(script1);
	}
	
	
	public void printEngines(ScriptEngineManager engine) {
		Iterator<ScriptEngineFactory> it = engine.getEngineFactories().iterator();
		while (it.hasNext()) {
			msg(it.next());
		}
	}
	
	
	public void log(String... s) {
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<s.length; ++i) {
			buf.append(s[i]);
			buf.append(' ');
		}
		msg(buf.toString());
	}
	
	
	public static void main(String[] args) throws Throwable {
		new TestScript().test();
	}
}
