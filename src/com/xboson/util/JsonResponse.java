/* CatfoOD 2017年11月2日 下午1:23:24 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.xboson.been.ResponseRoot;


/**
 * 专门返回 json 数据
 */
public class JsonResponse {
	
	private static final String attrname = "xBoson-JSON-response";
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ResponseRoot ret_root;
	private Builder jsbuilded;

	
	public JsonResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("JsonResponse is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
		this.request = request;
		this.response = response;
		
		jsbuilded = new Moshi.Builder();
		ret_root = new ResponseRoot();
	}
	
	
	public JsonResponse() {
		jsbuilded = new Moshi.Builder();
		ret_root = new ResponseRoot();
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
		jsbuilded.add(adapter);
	}
	
	
	/**
	 * 返回的 ResponseRoot 用于定制返回数据中的属性.
	 */
	public ResponseRoot getRoot() {
		return ret_root;
	}
	
	
	/**
	 * 应答客户端
	 * @param o - 可选的, 快速设置返回数据
	 * @throws IOException
	 */
	public void response(Object o) throws IOException {
		OutputStream out = response.getOutputStream();
		OutputStreamSinkWarp outwarp = new OutputStreamSinkWarp(out);

		if (o != null) {
			ret_root.setData(o, true);
		}
		
		// !! 需要优化, 中文乱码, PrintWriter 不乱码
		response.setHeader("content-type", "application/json; charset=utf-8");
		Moshi moshi = jsbuilded.build();
		moshi.adapter(ResponseRoot.class).toJson(outwarp, ret_root);
	}
	
	
	/**
	 * 仅用于调试, 不要在生产环境下使用.
	 */
	public String toString() {
		Moshi moshi = jsbuilded.build();
		return moshi.adapter(ResponseRoot.class).toJson(ret_root);
	}
}
