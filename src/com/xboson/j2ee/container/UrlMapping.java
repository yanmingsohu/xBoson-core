////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午3:18:56
// 原始文件路径: xBoson/src/com/xboson/j2ee/container/UrlMapping.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

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
import com.xboson.service.ServiceClassList;


public class UrlMapping implements ServletContextListener {

	private static final Map<String, XService> map = new HashMap<String, XService>();
	private static final Log log = LogFactory.create();
	
	
	/**
	 * 随时加入新的服务路由, 做成配置文件会被 hack
	 */
	@SuppressWarnings("unchecked")
	private void init_route() {
		Class<?>[] cs = ServiceClassList.list;
		for (int i=0; i<cs.length; ++i) {
			set((Class<? extends XService>) cs[i]);
		}
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
