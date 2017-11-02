/* CatfoOD 2017年11月2日 下午3:42:10 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.util.JsonResponse;

/**
 * 全部属性公共不可变方法
 */
public class CallData {

	public final HttpServletRequest req;
	public final HttpServletResponse resp; 
	public final UrlSplit url;
	public final JsonResponse json;
	public final SessionData sess;
	
	
	public CallData(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		this.req  = req;
		this.resp = resp;
		this.json = JsonResponse.get(req);
		this.sess = SessionData.get(req);
		this.url  = new UrlSplit(req);
	}
}
