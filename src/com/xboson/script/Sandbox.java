////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 上午11:52:58
// 原始文件路径: xBoson/src/com/xboson/script/Sandbox.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

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

import com.xboson.script.safe.SafeBinding;
import com.xboson.util.Tool;
import sun.font.Script;


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
	 */
	public WarpdScript warp(String filename, String code) throws ScriptException {
		return new WarpdScript(this, code, filename);
	}


  /**
   * 不指定文件名
   */
  public WarpdScript warp(String code) throws ScriptException {
    return new WarpdScript(this, code, "<warp>");
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


	public Invocable getGlobalFunc() {
		return (Invocable) js;
	}


	public Object bootstrap() throws ScriptException {
		if (ispred) return null;
		ispred = true;
		setFilename("<bootstrap>");
		InputStream script1 = getClass().getResourceAsStream("./bootstrap.js");
		return eval(script1);
	}


	public void bootstrapEnvReady() throws ScriptException {
    try {
      getGlobalFunc().invokeFunction("__env_ready");
    } catch(NoSuchMethodException e) {
      throw new ScriptException(e);
    }
  }


	public void bootstrapEnd() throws ScriptException {
		try {
			getGlobalFunc().invokeFunction("__boot_over");
    } catch(NoSuchMethodException e) {
			throw new ScriptException(e);
		}
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
