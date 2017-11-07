/* CatfoOD 2017年11月5日 上午11:33:42 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptException;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;


/**
 * 紧创建非常简单的工具类, 方便调试, 含有网络连接的复杂对象环境可以继承该类
 */
public class BasicEnvironment implements IEnvironment {
	
	private Log log = LogFactory.create();
	private List<IJSObject> objs;
	private ISysModuleProvider smp;

	
	public BasicEnvironment() {
		this(new SysModules());
	}


	public BasicEnvironment(ISysModuleProvider p) {
		smp = p;
		objs = new ArrayList<IJSObject>();
	}
	
	
	protected void setEnvObjectList(Class<?>[] list) {
		for (int i=0; i<list.length; ++i) {
			setEnvObject(list[i]);
		}
	}
	
	
	protected void setEnvObject(Class<?> c) {
		try {
			IJSObject jso = (IJSObject) c.newInstance();
			jso.init();
			objs.add(jso);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	
	public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
		Bindings bind = box.getBindings();
		Iterator<IJSObject> it = objs.iterator();
		
		while (it.hasNext()) {
			IJSObject o = it.next();
			String name = o.env_name();
			bind.put(name, o);
		}

		if (smp != null) {
			smp.config(box, runner);
		}
	}
	
	
	public void destory() {
		Iterator<IJSObject> it = objs.iterator();
		
		while (it.hasNext()) {
			IJSObject o = it.next();
			o.destory();
			it.remove();
		}
	}


	@Override
	protected void finalize() throws Throwable {
		destory();
	}


	@Override
	public ISysModuleProvider getModuleProvider() {
		return smp;
	}
}
