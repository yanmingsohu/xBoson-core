/* CatfoOD 2017年11月1日 上午11:29:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AuthCheck extends HttpFilter {

	private static final long serialVersionUID = 4001436349349397288L;

	
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		HttpSession session = request.getSession();
		System.out.println("Auth Filter " + session.getId());
		
		chain.doFilter(request, response);
	}
}
