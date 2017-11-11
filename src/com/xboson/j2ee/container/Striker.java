////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午1:02:16
// 原始文件路径: xBoson/src/com/xboson/j2ee/container/Striker.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			log.error(e.getMessage());
			response.setStatus(500);
			jr.getRoot().setError(e);
			jr.response();
		}
	}
}
