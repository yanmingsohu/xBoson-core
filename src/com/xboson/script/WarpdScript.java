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
	private CompiledScript cs;
	private String filename;
  private ICodeRunner crun;
	private Object warpreturn;
	
	
	WarpdScript(Sandbox box, String code, String filename) throws ScriptException {
		this.box  	  = box;
		this.code 	  = code;
		this.module   = new Module();
		this.filename = filename;
		warp();
	}
	
	
	private void warp() throws ScriptException {
	  box.setFilename(filename);
		cs = box.compile(
				"__warp_main(function(require, module, __dirname"
        + ", __filename, exports, console) {"
				+ code 
				+ "\n})");
	}
	
	
	public Object call() throws ScriptException {
		jso = (AbstractJSObject) cs.eval();
    warpreturn = jso.call(module, module, crun);
		module.loaded = true;
		return warpreturn;
	}
	
		
	public Module getModule() {
		return module;
	}
	
	
	public void setCodeRunner(ICodeRunner crun) {
		this.crun = crun;
	}
}
