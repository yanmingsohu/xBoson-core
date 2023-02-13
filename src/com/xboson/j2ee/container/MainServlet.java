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
// 文件创建日期: 2017年11月1日 上午11:26:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/MainServlet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;


/**
 * 实现了一个轻量级路由
 *
 * @see com.xboson.init.Startup 配置到容器
 */
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 5854315900895352428L;
	private final Log log;


	public MainServlet() {
    log = LogFactory.create("service-route");
  }


  /**
	 * 参数通过 URL 传递
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		CallData cd = new CallData(req, resp);
		XService sv = UrlMapping.getService(cd.url);
		
		if (sv == null) {
			throw new XBosonException.NoService(cd.url.getName());
		}

		if (sv.needLogin()) {
      sv.checkLoging(cd);
    }

    log.debug(cd.url.getName(), sv.getClass().getName());

		try {
      sv.service(cd);

      if (!cd.xres.isResponsed()) {
        cd.xres.responseMsg("unknow return", 999);
      }
    } catch(XBosonException e) {
			throw e;
		} catch(RuntimeException r) {
			throw r;
    } catch(Exception e) {
		  throw new XBosonException(e);
    }
  }


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
