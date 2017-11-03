/* CatfoOD 2017年11月3日 上午11:52:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 * 一个编译好的脚本, 可以反复运行.
 */
public class Sandbox {
	
	private ScriptEngine js;
	private Bindings bind;
	private ScriptContext context;

	
	public Sandbox() throws ScriptException {
		ScriptEngineManager engine = new ScriptEngineManager();
		ScriptEngine js = engine.getEngineByName("javascript");
		this.js 		= js;
		this.bind 		= js.getBindings(ScriptContext.ENGINE_SCOPE);
		this.context 	= js.getContext();
	}
	
	
	public void precomplile() throws ScriptException {
		InputStream script1 = getClass().getResourceAsStream("./precomplile.js");
		compile(script1);
	}
	
	
	public void compile(String code) throws ScriptException {
		js.eval(code);
	}
	
	
	public void compile(InputStream instream) throws ScriptException {
		try {
			Reader reader = new InputStreamReader(instream);
			js.eval(reader);
		} finally {
			try {
				instream.close();
			} catch(IOException e) {
				// nothing.
			}
		}
	}
	
	
	public Object call(String functionName) throws NoSuchMethodException, ScriptException {
		Invocable inv = (Invocable) js;
		return inv.invokeFunction(functionName);
	}
	
	
	public Bindings getBindings() {
		return bind;
	}
	
	
	public ScriptContext getContext() {
		return context;
	}
}
