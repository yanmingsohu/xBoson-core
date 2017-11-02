/* CatfoOD 2017年11月2日 下午2:43:15 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionData {
	
	private static final String attrname = "xBoson-session-data";
	

	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	
	public SessionData(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("SessionData is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
		this.request = request;
		this.response = response;
	}

	
	public static SessionData get(HttpServletRequest request) throws ServletException {
		SessionData sd = (SessionData) request.getAttribute(attrname);
		if (sd == null) {
			throw new ServletException("SessionData not init");
		}
		return sd;
	}
}