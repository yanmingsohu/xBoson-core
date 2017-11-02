/* CatfoOD 2017年11月2日 下午3:00:14 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import javax.servlet.http.HttpServletRequest;

/**
 * 将请求 url 分解, 用于路由映射
 * 当入参为 /a/b/c
 * 	 name = /a
 *   last = /b/c
 */
public class UrlSplit {
	
	/** url 中的首个路径 */
	private String name;
	/** url 中的后续路径 */
	private String last;


	public UrlSplit(HttpServletRequest req) {
		String cp = req.getContextPath();
		String rq = req.getRequestURI();
		split( rq.substring(cp.length(), rq.length()) );
	}
	
	
	public UrlSplit(String s) {
		split(s);
	}
	
	
	public void split(String s) {
		if (s == null) 
			throw new RuntimeException("cannot split null string url.");
		
		int a = 0;
		if (s.charAt(0) == '/') a = 1;
		a = s.indexOf('/', a);
		
		if (a >= 0) {
			name = s.substring(0, a);
			last = s.substring(a, s.length());
		} else {
			name = s;
			last = null;
		}
	}
	
	
	/**
	 * 将 last 再次拆分
	 */
	public UrlSplit sub() {
		return new UrlSplit(last);
	}
	
	
	public String toString() {
		return name + " : " + last;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getLast() {
		return last;
	}
}
