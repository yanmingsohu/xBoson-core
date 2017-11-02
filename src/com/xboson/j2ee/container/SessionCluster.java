/* CatfoOD 2017年11月1日 上午11:57:34 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.xboson.been.SessionData;
import com.xboson.util.AES;
import com.xboson.util.SessionID;


public class SessionCluster extends HttpFilter implements HttpSessionListener {
	
	private static final long serialVersionUID = -6654306025872001022L;
	private static final String cookieName = "xBoson";
	
	private static byte[] sessionPassword = null;
	private static int sessionTimeout = 0;
	

	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("s Create");
	}

	
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("s Destory");
	}

	
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		Cookie ck = SessionID.getCookie(cookieName, request);
		if (ck == null) {
			ck = new Cookie(cookieName, SessionID.generateSessionId(sessionPassword));
			ck.setHttpOnly(true);
			ck.setMaxAge(sessionTimeout);
			response.addCookie(ck);
		} else {
			SessionID.checkSessionId(sessionPassword, ck.getValue());
		}
		new SessionData(request, response);
		System.out.println("Session ID: " + ck.getValue());
		chain.doFilter(request, response);
	}


	public void init(FilterConfig filterConfig) throws ServletException {
		sessionTimeout = 
			60 * filterConfig.getServletContext().getSessionTimeout();
		sessionPassword = 
			AES.aesKey( filterConfig.getInitParameter("session_password") );
	}
}
