////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午1:23:24
// 原始文件路径: xBoson/src/com/xboson/util/JsonResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.moshi.JsonAdapter;
import com.xboson.been.ResponseRoot;


/**
 * 专门返回 json 数据, 并不需要考虑在从 json 字符串返回对象.
 */
public class JsonResponse {
	
	private static final String attrname = "xBoson-JSON-response";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ResponseRoot ret_root;
	private JsonAdapter<ResponseRoot> jadapter;

	
	public JsonResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		this();
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("JsonResponse is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
		this.request = request;
		this.response = response;
	}
	
	
	public JsonResponse() {
		ret_root = new ResponseRoot();
		jadapter = Tool.getAdapter(ResponseRoot.class);
	}
	
	
	public static JsonResponse get(HttpServletRequest request) throws ServletException {
		JsonResponse jr = (JsonResponse) request.getAttribute(attrname);
		if (jr == null) {
			throw new ServletException("JsonResponse not bind to request");
		}
		return jr; 
	}
	
	
	/**
	 * 将 been 转换为 json 需要 been 的转换适配器, 在这里注册适配器
	 * @param c -- 适配器的类
	 */
	public void regJsonAdapter(Object adapter) {
		Tool.regJsonAdapter(adapter);
	}
	
	
	/**
	 * 返回的 ResponseRoot 用于定制返回数据中的属性.
	 */
	public ResponseRoot getRoot() {
		return ret_root;
	}
	
	
	/**
	 * 应答客户端
	 * @param o - 快速设置返回数据
	 * @throws IOException
	 */
	public void response(Object o) throws IOException {
		if (o == null) {
			throw new NullPointerException("parm null");
		}
		ret_root.setData(o, true);
		response();
	}
	
	
	public void response() throws IOException {
		OutputStream out = response.getOutputStream();
		OutputStreamSinkWarp outwarp = new OutputStreamSinkWarp(out);
		
		String jsonp = request.getParameter("jsonp");
		
		if (jsonp == null || jsonp == "false" || jsonp == "0") {
			response.setHeader("content-type", "application/json; charset=utf-8");
			jadapter.toJson(outwarp, ret_root);
		} else {
			response.setHeader("content-type", "application/javascript; charset=utf-8");
			outwarp.writeUtf8(jsonp);
			outwarp.writeUtf8("(");
			jadapter.toJson(outwarp, ret_root);
			outwarp.writeUtf8(");");
		}
	}
	
	
	/**
	 * 仅用于调试, 不要在生产环境下使用.
	 */
	public String toString() {
		return ret_root.toString();
	}


	public String toJSON() {
	  return ret_root.toJSON();
  }
}
