/* CatfoOD 2017年11月2日 上午10:17:30 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import com.xboson.util.AES;
import com.xboson.util.SessionID;

public class TestSession extends Test {

	public void test() throws Throwable {
		byte[] ps = AES.aesKey("abc");
		msg("getLength:" + ps.length);
		
		String sessionid = SessionID.generateSessionId(ps);
		msg("Session ID: " + sessionid);
		
		if (!SessionID.checkSessionId(ps, sessionid)) {
			throw new Exception("wrong");
		}
		
		if (SessionID.checkSessionId(ps, "f"+sessionid)) {
			throw new Exception("Fail: not checked bad sessionid");
		}
		
		success("Session ok.");
	}

}
