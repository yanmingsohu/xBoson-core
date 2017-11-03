/* CatfoOD 2017年11月2日 下午3:18:56 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.xboson.been.UrlSplit;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.service.Fail;


public class UrlMapping implements ServletContextListener {

	private static final Map<String, XService> map = new HashMap<String, XService>();
	private static final Log log = LogFactory.create();
	
	
	/**
	 * 随时加入新的服务路由, 做成配置文件会被 hack
	 */
	private void init_route() {
		set(com.xboson.service.Login.class);
	}
	
	
	
	private void set(Class<? extends XService> sclass) {
		XPath path = sclass.getAnnotation(XPath.class);
		String pname = null;
		if (path != null) {
			pname = path.value();
		} else {
			log.error("REG skip: cannot fonund XPath anno on " + sclass);
			return;
		}
		
		try {
			XService service = sclass.newInstance();
			map.put(pname, service);
			log.info("REG service: ", pname, "-", sclass.getName());
		} catch(Exception e) {
			XService fail = new Fail(e);
			map.put(pname, fail);
			log.error("REG fail: ", e.getMessage());
		}
	}
	
	
	public static XService getService(String path) {
		return map.get(path);
	}
	
	
	public static XService getService(UrlSplit url) {
		return map.get(url.getName());
	}
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Collection<XService> list = map.values();
		Iterator<XService> it = list.iterator();
		
		while (it.hasNext()) {
			XService x = it.next();
			String name = x.getClass().getName();
			try {
				x.destroy();
				log.info("Service", name, "destoryed");
			} catch(Exception e) {
				log.error("Destory service", name, "fail:", e.getMessage());
			}
		}
		
		map.clear();
	}

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		init_route();
	}
}
