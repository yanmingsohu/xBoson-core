////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月1日 上午11:57:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/SessionCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

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
		Config cfg = SysConfig.me().readConfig();
		sessionTimeout = cfg.sessionTimeout;
		sessionPassword = AES.aesKey( cfg.sessionPassword );
	}
}
