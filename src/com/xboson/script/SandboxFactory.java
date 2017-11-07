/* CatfoOD 2017年11月5日 上午10:52:43 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import com.xboson.script.safe.BlockAllFilter;
import com.xboson.script.safe.SafeBinding;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import java.util.*;

/**
 * 线程安全, 并对线程优化的沙箱工厂
 */
public class SandboxFactory {
	
	private static ThreadLocal<NashornScriptEngineFactory> seml = new ThreadLocal<NashornScriptEngineFactory>();
	private static Log log = LogFactory.create();
	private static BlockAllFilter blockall = new BlockAllFilter();


	/**
	 * 创建一个独立的沙箱
	 * @throws ScriptException 
	 */
	public static Sandbox create() throws ScriptException {
		ScriptEngine se = getEM().getScriptEngine(blockall);
		return new Sandbox(se);
	}
	
	
	public static NashornScriptEngineFactory getEM() {
		NashornScriptEngineFactory em = seml.get();
		if (em == null) {
			em = new NashornScriptEngineFactory();
			seml.set(em);
		}
		return em;
	}
	
	
	public static void version() {
		NashornScriptEngineFactory n = getEM();
		log.info("Script ENGINE:", 
				"[" + n.getEngineName(), n.getEngineVersion() + "]",
				"[" + n.getLanguageName(), n.getLanguageVersion() + "]");
	}
}
