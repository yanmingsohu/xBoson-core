////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午2:43:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/SessionData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionData {
	
	private static final String attrname = "xBoson-session-data";
	
	
	public SessionData() {
	}
	
	
	/**
	 * 将 this 绑定到 request attribute 上
	 */
	public SessionData(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("SessionData is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
	}

	
	public static SessionData get(HttpServletRequest request) throws ServletException {
		SessionData sd = (SessionData) request.getAttribute(attrname);
		if (sd == null) {
			throw new ServletException("SessionData not init");
		}
		return sd;
	}
}
