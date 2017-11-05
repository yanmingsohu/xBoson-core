/* CatfoOD 2017年11月3日 上午10:43:25 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

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
import com.xboson.script.Application;
import com.xboson.script.BasicEnvironment;
import com.xboson.script.IVirtualFileSystem;
import com.xboson.script.Sandbox;
import com.xboson.script.SandboxFactory;
import com.xboson.script.WarpdScript;
import com.xboson.util.StringBufferOutputStream;

/**
 * 1 秒内调用函数次数
 * NODE js :  9099763.1
 * java js : 36106378.8
 * 
 * https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions
 */
public class TestScript extends Test {

	
	public TestScript() throws ScriptException {
		new LogFactory.Init().setType("TestOut");
		SandboxFactory.version();
	}
	

	public void test() throws Exception {
//		independent_sandbox();
//		speedtest();
//		memoryuse();
//		closed();
		hack();
		application();
	}
	
	
	public void application() throws Exception {
		BasicEnvironment env = new BasicEnvironment();
		FixFile vfs = new FixFile();
		vfs.putfile("/index.js", "./check-safe.js");
		vfs.putfile("/a.js", "./check-safe.js");
		
		Application app = new Application(env, vfs);
		app.run("/index.js");
		success("Application");
	}
	
	
	static public class FixFile implements IVirtualFileSystem {
		/** <virtual_filename, code> */
		Map<String, String> map = new HashMap<String, String>();
		public FixFile() {}
		
		void putcode(String name, String code) {
			map.put(name, code);
		}
		
		@SuppressWarnings("resource")
		void putfile(String name, String file) throws IOException {
			InputStream is = getClass().getResourceAsStream(file);
			StringBufferOutputStream sbos = new StringBufferOutputStream();
			sbos.write(is);
			map.put(name, sbos.toString());
		}

		@Override
		public String readFile(String path) throws IOException {
			return map.get(path);
		}

		@Override
		public String getID() {
			return "FIX";
		}
	}
	
	
	public void hack() throws Exception {
		Sandbox sandbox = SandboxFactory.create();
		sandbox.precompile();
		BasicEnvironment env = new BasicEnvironment();
		env.config(sandbox);
		sandbox.freezeGlobal();
		
		Object o = null;
		
		sandbox.warp("console = null; console.log=null; console.a='bad', console.log('hello', console.a)").call();
		sandbox.warp("if (console.a) throw new Error('console.a is changed.')");
		success("cannot modify java object");
		
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
		Integer aa = (Integer) a.call("g");
		Integer bb = (Integer) b.call("g");
		if (aa == 1 && bb == 2) {
			success("independent sandbox");
		} else {
			throw new Exception("sandbox value cross");
		}
	}
	
	
	public void speedtest() throws ScriptException {
		InputStream script1 = getClass().getResourceAsStream("./TestScript.js");
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
