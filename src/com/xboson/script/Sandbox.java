/* CatfoOD 2017年11月3日 上午11:52:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.xboson.util.Tool;


/**
 * 一个编译好的脚本, 可以反复运行.
 */
public class Sandbox {
	
	private ScriptEngine js;
	private Bindings bind;
	private ScriptContext context;
	private boolean ispred = false;
	private Compilable cpl;

	
	Sandbox(ScriptEngine engine) throws ScriptException {
		this.js 			= engine;
		this.bind 		= js.getBindings(ScriptContext.ENGINE_SCOPE);
		this.context 	= js.getContext();
		
		if (js instanceof Compilable) {
			cpl = (Compilable) js;
		} else {
			throw new ScriptException("cannot compile");
		}
	}
		
	
	public Object precompile() throws ScriptException {
		if (ispred) return null;
		ispred = true;
		setFilename("<boot>");
		InputStream script1 = getClass().getResourceAsStream("./precomplile.js");
		return eval(script1);
	}
	
	
	/**
	 * 冻结对象
	 * @param name - 如果为空则冻结全局对象
	 * @throws ScriptException
	 */
	public void freeze(String name) throws ScriptException {
		if (name == null) {
			throw new ScriptException("parm 'name' is null");
		}
		eval("Object.freeze(" + name + ")");
	}
	
	
	/**
	 * 冻结全局对象, 即使 hack 也无法修改全局绑定的工具对象,
	 * 无法在全局定义变量/函数.
	 * 
	 * !!锁住导致文件名无法与脚本绑定, 抛出的异常没有文件路径
	 * 
	 * @throws ScriptException
	 */
	public void freezeGlobal() throws ScriptException {
		freeze("this");
	}
	
	
	/**
	 * 将代码包装到函数中, 并且使用 nodejs 语法
	 * @param code
	 * @return
	 * @throws ScriptException
	 */
	public WarpdScript warp(String code) throws ScriptException {
		return new WarpdScript(this, code);
	}
	
	
	public Object eval(String code) throws ScriptException {
		// System.out.println("CODE: " + code);
		return js.eval(code);
	}
	
	
	public Object eval(InputStream instream) throws ScriptException {
		try {
			Reader reader = new InputStreamReader(instream);
			return js.eval(reader);
		} finally {
			Tool.close(instream);
		}
	}
	
	
	public CompiledScript compile(String code) throws ScriptException {
		return cpl.compile(code);
	}
	
	
	public Object call(String functionName) throws NoSuchMethodException, ScriptException {
		Invocable inv = (Invocable) js;
		return inv.invokeFunction(functionName);
	}
	
	
	/**
	 * 在 compile 之前设置有效
	 */
	public void setFilename(String name) {
		context.setAttribute(ScriptEngine.FILENAME, name, ScriptContext.ENGINE_SCOPE);
	}
	
	
	public Bindings getBindings() {
		return bind;
	}
	
	
	public ScriptContext getContext() {
		return context;
	}
}
