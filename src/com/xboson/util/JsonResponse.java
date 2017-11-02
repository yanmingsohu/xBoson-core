/* CatfoOD 2017年11月2日 下午1:23:24 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;


/**
 * 专门返回 json 数据
 */
public class JsonResponse {
	
	private static final String attrname = "xBoson-JSON-response";
	private static Builder jsbuilded = new Moshi.Builder();
	
	private HttpServletRequest request;
	private HttpServletResponse response;

	
	public JsonResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		if (request.getAttribute(attrname) != null) {
			throw new ServletException("JsonResponse is bind to HttpServletRequest");
		}
		request.setAttribute(attrname, this);
		this.request = request;
		this.response = response;
		
	}
	
	
	public static JsonResponse get(HttpServletRequest request) throws ServletException {
		JsonResponse jr = (JsonResponse) request.getAttribute(attrname);
		if (jr == null) {
			throw new ServletException("JsonResponse not init");
		}
		return jr; 
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void response(Object o) throws IOException {
		PrintWriter out = response.getWriter();
		
		Moshi moshi = jsbuilded.build();
		Class c = o.getClass();
		String str = moshi.adapter(c).toJson(o);
		
		response.setHeader("content-type", "application/json; charset=utf-8");
		out.write(str);
	}
}
