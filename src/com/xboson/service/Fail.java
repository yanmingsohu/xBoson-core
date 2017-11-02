/* CatfoOD 2017年11月2日 下午4:16:17 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.service;

import java.io.IOException;

import javax.servlet.ServletException;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XService;

/**
 * 服务注册失败占位符
 */
public class Fail extends XService {
	
	private ServletException err;
	
	
	public Fail(ServletException e) {
		err = e;
	}
	
	
	public Fail(Exception e) {
		err = new ServletException(e);
	}


	@Override
	public int service(CallData data) throws ServletException, IOException {
		throw err;
	}
}
