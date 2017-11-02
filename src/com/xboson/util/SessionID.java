/* CatfoOD 2017年11月2日 下午12:28:55 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class SessionID {

	private static final Random r = new SecureRandom();
	
	
	/**
	 * 在请求中寻找指定名称的 cookie
	 */
	public static Cookie getCookie(String name, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;
		
		for (int i=0; i<cookies.length; ++i) {
			if (cookies[i].getName().equalsIgnoreCase(name)) {
				return cookies[i];
			}
		}
		return null;
	}
	
	
	public static String generateSessionId(final byte[] password) throws ServletException {
		byte[] bytes = new byte[164];
		r.nextBytes(bytes);
		bytes = AES.Encode(bytes, password);
		String ret = Base64.getEncoder().encodeToString(bytes);
		return ret;
	}
	
	
	/**
	 * 检查 session 是否安全, 错误的 sid 会抛出异常
	 * @throws ServletException
	 */
	public static void checkSessionId(byte[] ps, String sid) throws ServletException {
		try {
			byte[] data = Base64.getDecoder().decode(sid);
			AES.Decode(data, ps);
		} catch(Exception e) {
			ServletException t = new ServletException("check session fail");
			t.addSuppressed(e);
			throw t;
		}
	}
}
