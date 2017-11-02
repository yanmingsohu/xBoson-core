/* CatfoOD 2017年11月2日 下午1:02:16 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.been.ResponseRoot;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.JsonResponse;

/**
 * 抓住所有异常, 编码转换, 等初始化操作
 */
public class Striker extends HttpFilter {

	private static final long serialVersionUID = 8889985807692963369L;
	private final Log log = LogFactory.create();

	
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		response.setCharacterEncoding("utf8");
		request.setCharacterEncoding("utf8");
		JsonResponse jr = null;
		
		try {
			jr = new JsonResponse(request, response);
			chain.doFilter(request, response);
		} catch(Throwable e) {
			response.setStatus(500);
			ResponseRoot r = new ResponseRoot(e);
			jr.response(r);
			log.error(e.getMessage());
		}
	}
}
