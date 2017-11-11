////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月5日 下午2:43:58
// 原始文件路径: xBoson/src/com/xboson/script/Application.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

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
		sandbox.bootstrapEnvReady();
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
			modcache.remove(path);
		}
	}
}
