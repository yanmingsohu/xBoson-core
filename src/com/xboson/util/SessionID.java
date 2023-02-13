/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 22017年11月2日 下午12:28:55
// 原始文件路径: xBoson/src/com/xboson/util/SessionID.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class SessionID {

	private static final Random r = new SecureRandom();
	private static final byte[] sign = "J.yanming".getBytes();
	private static final int sessionLength = 164 + sign.length;
	
	
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
	
	
	public static String generateSessionId(final byte[] password)
					throws ServletException {
		byte[] data = new byte[sessionLength];
		r.nextBytes(data);
		
		int begin = data.length - sign.length;
		for (int i=begin; i<data.length; ++i) {
			data[i] = sign[i - begin];
		}
		
		data = AES.Encode(data, password);
		String ret = Base64.getEncoder().encodeToString(data);
		return ret;
	}
	
	
	/**
	 * 检查 session 是否安全, 错误的 sid 会抛出异常
	 * @throws ServletException
	 */
	public static boolean checkSessionId(byte[] ps, String sid)
					throws ServletException {
		try {
			byte[] data = Base64.getDecoder().decode(sid);
			data = AES.Decode(data, ps);
			
			int begin = data.length - sign.length;
			for (int i=begin; i<data.length; ++i) {
				if (data[i] != sign[i - begin])
					return false;
			}
			
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}
