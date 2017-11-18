////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午1:23:24
// 原始文件路径: xBoson/src/com/xboson/util/XResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.moshi.JsonAdapter;
import com.xboson.been.ResponseRoot;
import com.xboson.been.XBosonException;
import com.xboson.util.OutputStreamSinkWarp;
import com.xboson.util.Tool;


/**
 * 专门返回 xres 数据, 并不需要考虑在从 xres 字符串返回对象.
 * 'jsonp' 当请求中有该参数时, 进行 jsonp 应答.
 */
public class XResponse {
	
	private static final String attrname  = "xBoson-X-response";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ResponseRoot ret_root;
	private IXResponse res_impl;
	private boolean is_responsed = false;

	
	public XResponse(HttpServletRequest request, HttpServletResponse response)
          throws ServletException {
		this();
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("XResponse is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
		this.request = request;
		this.response = response;
	}
	
	
	public XResponse() {
		this.ret_root = new ResponseRoot();
    this.res_impl = ResponseTypes.get();
	}
	
	
	public static XResponse get(HttpServletRequest request)
          throws ServletException {
		XResponse jr = (XResponse) request.getAttribute(attrname);
		if (jr == null) {
			throw new ServletException("XResponse not bind to request");
		}
		return jr; 
	}
	
	
	/**
	 * 返回的 ResponseRoot 用于定制返回数据中的属性.
	 */
	public ResponseRoot getRoot() {
		return ret_root;
	}


  /**
   * 设置应答方式
   * @param typename 可选的: json / xml, 无效的名称会抛出异常
   * @see ResponseTypes
   */
	public void setResponseType(String typename) {
    res_impl = ResponseTypes.get(typename);
  }
	
	
	/**
	 * 立即应答客户端
	 * @param data 快速设置返回数据
	 * @throws IOException
	 */
	public void response(Object data) throws IOException {
    response(data, 0);
	}


  /**
   * 立即应答客户端
   * @param data 快速设置返回数据
   * @param code 返回码
   * @throws IOException
   */
	public void response(Object data, int code) throws IOException {
		if (data == null) {
			throw new NullPointerException("parm null");
		}
		ret_root.setData(data, true);
		ret_root.setCode(code);
		response();
	}
	
	
	public void response() throws IOException {
	  if (is_responsed)
	    throw new XBosonException("is responsed");

    res_impl.response(request, response, ret_root);
    is_responsed = true;
	}


	public boolean isResponsed() {
	  return is_responsed;
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
