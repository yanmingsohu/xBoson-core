/* CatfoOD 2017年11月1日 上午11:26:10 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.been.CallData;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;


public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 5854315900895352428L;
	private final Log log = LogFactory.create("route");

	
	/**
	 * 参数通过 URL 传递
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		CallData cd = new CallData(req, resp);
		XService sv = UrlMapping.getService(cd.url);
		
		if (sv == null) {
			throw new ServletException("Not found service: " + cd.url.getName());
		}
		
		log.debug(cd.url.getName(), sv.getClass().getName());
		sv.service(cd);
	}

	
	/**
	 * 参数在 body 中, 类型为 json, 不接受任何 URL 参数.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.append("Post is work");
		out.close();
	}

}
