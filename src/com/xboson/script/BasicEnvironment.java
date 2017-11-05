/* CatfoOD 2017年11月5日 上午11:33:42 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptException;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.env.Console;

/**
 * 紧创建非常简单的工具类, 方便调试, 含有网络连接的复杂对象环境可以继承该类
 */
public class BasicEnvironment implements IEnvironment {
	
	private Log log = LogFactory.create();
	private List<IJSObject> objs;

	
	public BasicEnvironment() {
		objs = new ArrayList<IJSObject>();
		
		setEnvObjectList(new Class<?>[]{
			Console.class,
		});
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
	
	
	public void config(Sandbox box) throws ScriptException {
		Bindings bind = box.getBindings();
		Iterator<IJSObject> it = objs.iterator();
		
		while (it.hasNext()) {
			IJSObject o = it.next();
			String name = o.env_name();
			bind.put(name, o);
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
	
}
