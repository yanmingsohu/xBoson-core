/* CatfoOD 2017年11月5日 上午9:56:07 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.xboson.been.Module;

import jdk.nashorn.api.scripting.AbstractJSObject;


public class WarpdScript {

	private AbstractJSObject jso;
	private Sandbox box;
	private String code;
	private Module module;
	private ICodeRunnner crun;
	private CompiledScript cs;
	
	
	WarpdScript(Sandbox box, String code) throws ScriptException {
		this.box  	= box;
		this.code 	= code;
		this.module = new Module();
		warp();
	}
	
	
	private void warp() throws ScriptException {
		cs = box.compile(
				"__warp_main(function(require, module, __dirname, __filename, exports) {" 
				+ code 
				+ "\n})");
	}
	
	
	public Object call() throws ScriptException {
		jso = (AbstractJSObject) cs.eval();
		return jso.call(module, module, crun);
	}
	
		
	public Module getModule() {
		return module;
	}
	
	
	public void setCodeRunner(ICodeRunnner crun) {
		this.crun = crun;
	}
}
