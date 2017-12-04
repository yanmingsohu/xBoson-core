////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月5日 上午9:56:07
// 原始文件路径: xBoson/src/com/xboson/script/WarpdScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.xboson.been.Module;

import com.xboson.been.XBosonException;
import com.xboson.util.IConstant;
import jdk.nashorn.api.scripting.AbstractJSObject;

import java.nio.ByteBuffer;


public class WarpdScript {

	private AbstractJSObject jso;
	private Sandbox box;
	private Module module;
	private CompiledScript cs;
	private String filename;
  private ICodeRunner crun;
	private Object warpreturn;
	private ByteBuffer code;
	private String strcode;


	WarpdScript(Sandbox box, ByteBuffer code, String filename)
					throws ScriptException {
		this.box  	  = box;
		this.code 	  = code;
		this.module   = new Module();
		this.filename = filename;
		warp();
	}


	WarpdScript(Sandbox box, String code, String filename)
					throws ScriptException {
		this.box  	  = box;
		this.strcode  = code;
		this.module   = new Module();
		this.filename = filename;
		warp();
	}
	
	
	private void warp() throws ScriptException {
	  box.setFilename(filename);
		cs = box.compile(
				"__warp_main(function(require, module, __dirname"
        + ", __filename, exports, console) {"
				+ getStringCode()
				+ "\n})");
	}


	private String getStringCode() {
		if (strcode != null) {
			return strcode;
		}
		else if (code != null) {
			strcode = new String(code.array(), IConstant.CHARSET);
			return strcode;
		}
		else {
			throw new XBosonException("can not get code");
		}
	}
	
	
	public Object call() {
		try {
			jso = (AbstractJSObject) cs.eval();
			warpreturn = jso.call(module, module, crun);
			module.loaded = true;
			return warpreturn;
		} catch(Exception e) {
			throw new JScriptException(e, code);
		}
	}
	
		
	public Module getModule() {
		return module;
	}
	
	
	public void setCodeRunner(ICodeRunner crun) {
		this.crun = crun;
	}
}
