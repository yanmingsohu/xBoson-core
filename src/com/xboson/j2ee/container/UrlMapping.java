/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 2017年11月2日 下午3:18:56
// 原始文件路径: xBoson/src/com/xboson/j2ee/container/UrlMapping.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import com.xboson.been.UrlSplit;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.service.Fail;
import com.xboson.service.ServiceClassList;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UrlMapping extends OnExitHandle {

	private static final Map<String, XService> map = new ConcurrentHashMap<>();
  private static UrlMapping instance;
	private final Log log = LogFactory.create();



	public static UrlMapping me() {
		if (instance == null) {
			synchronized (UrlMapping.class) {
			  if (instance == null) {
          instance = new UrlMapping();
        }
      }
    }
		return instance;
	}


	private UrlMapping() {
		init_route();
		log.info("Initialization Success");
	}

	@Override
	protected void exit() {
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

}
