/* CatfoOD 2017年11月5日 下午2:43:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import com.xboson.been.Module;

/**
 * 应用存储在二级目录中, 第一级是模块名, 第二级是接口名
 * 不支持多线程.
 */
public class Application implements ICodeRunner {
	
	private Sandbox sandbox;
	@SuppressWarnings("unused")
	private IEnvironment env;
	private IVirtualFileSystem vfs;
	private Map<String, WarpdScript> modcache;

	
	public Application(IEnvironment env, IVirtualFileSystem vfs) throws ScriptException {
		this.env = env;
		this.vfs = vfs;
		modcache = new HashMap<String, WarpdScript>();
		sandbox  = SandboxFactory.create();
		
		sandbox.bootstrap();
		env.config(sandbox, this);
		sandbox.bootstrapEnd();
	}
	
	
	public Module run(String modname, String apiname) throws IOException, ScriptException {
		return run('/' + modname + '/' + apiname);
	}
	
	
	/**
	 * 运行路径上的脚本, 返回脚本的运行结果
	 */
	public Module run(String path) throws IOException, ScriptException {
		if (path.charAt(0) != '/')
			throw new IOException("path must begin with '/' " + path);
		
		WarpdScript ws = modcache.get(path);
		if (ws != null) {
			return ws.getModule();
		}
		
		String code = vfs.readFile(path);
		if (code == null) {
			throw new IOException("get null code " + path);
		}

		ws = new WarpdScript(sandbox, code, path);
		ws.setCodeRunner(this);
		modcache.put(path, ws);
		
		Module mod = ws.getModule();
		mod.filename = path;
		mod.id = '/' + vfs.getID() + path;
		mod.paths = new String[] { "/js_modules" };

		ws.call();
		
		return mod;
	}
	
	
	/**
	 * 通知应有, 有脚本被修改
	 */
	public synchronized void changed(String path) {
		WarpdScript ws = modcache.get(path);
		if (ws != null) {
			ws.getModule().loaded = false;
		}
	}
}
