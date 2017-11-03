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

import com.xboson.been.Config;
import com.xboson.been.SessionData;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.AES;
import com.xboson.util.SessionID;
import com.xboson.util.SysConfig;


public class SessionCluster extends HttpFilter {
	
	private static final long serialVersionUID = -6654306025872001022L;
	private static final String cookieName = "xBoson";
	private static final Log log = LogFactory.create("session");
	
	private static byte[] sessionPassword = null;
	private static int sessionTimeout = 0;
	
	
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		Cookie ck = SessionID.getCookie(cookieName, request);
		if (ck == null) {
			ck = createCookie(response);
		} else {
			if (!SessionID.checkSessionId(sessionPassword, ck.getValue()) ) {
				ck = createCookie(response);
			}
		}
		new SessionData(request, response);
		log.debug("Session ID: " + ck.getValue());
		chain.doFilter(request, response);
	}
	
	
	private Cookie createCookie(HttpServletResponse response) throws ServletException {
		Cookie ck = new Cookie(cookieName, SessionID.generateSessionId(sessionPassword));
		ck.setHttpOnly(true);
		ck.setMaxAge(sessionTimeout);
		response.addCookie(ck);
		return ck;
	}


	public void init(FilterConfig filterConfig) throws ServletException {
		Config cfg = SysConfig.getInstance().readConfig();
		sessionTimeout = cfg.sessionTimeout;
		sessionPassword = AES.aesKey( cfg.sessionPassword );
	}
}
