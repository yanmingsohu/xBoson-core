/* CatfoOD 2017年11月2日 下午3:22:04 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.io.IOException;

import javax.servlet.ServletException;

import com.xboson.been.CallData;


public abstract class XService {
	
	/**
	 * 子类实现该方法, 当服务被调用, 进入该方法中
	 * @return 如果没有错误返回 0, 否则返回错误码
	 */
	public abstract int service(CallData data) throws ServletException, IOException;
	
	
	/**
	 * 子类重写该方法, 当服务器终止时调用
	 */
	public void destroy() {
		Class<?> c = XService.this.getClass();
		System.out.println("Service " + c.getName() + " destory");
	}

}
