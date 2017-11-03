/* CatfoOD 2017年11月3日 上午10:43:25 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import java.io.InputStream;
import java.util.Iterator;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.xboson.script.Sandbox;

/**
 * NODE js :  9099763.1
 * java js : 36106378.8
 */
public class TestScript extends Test {

	public void test() throws Exception {
		independent_sandbox();
//		speedtest();
//		memoryuse();
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
			a[i] = new Sandbox();
		}
		memuse();
		for (int i=0; i<a.length; ++i) {
			a[i].compile("a=1");
		}
		memuse();
	}
	
	
	public void independent_sandbox() throws Exception {
		Sandbox a = new Sandbox();
		Sandbox b = new Sandbox();
		String c = "function g() { return a; }";
		a.compile(c);
		b.compile(c);
		a.compile("a = 1;");
		b.compile("a = 2;");
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
		Sandbox s = new Sandbox();
		s.getBindings().put("console", this);
		s.compile(script1);
	}
	
	
	public static void main(String[] args) throws Throwable {
		new TestScript().test();
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
}
