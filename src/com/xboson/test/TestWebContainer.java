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
// 文件创建日期: 2017年11月2日 上午10:17:30
// 原始文件路径: xBoson/src/com/xboson/test/TestSession.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.NameCache;
import com.xboson.been.ResponseRoot;
import com.xboson.util.AES;
import com.xboson.util.SessionID;

import javax.swing.*;

public class TestWebContainer extends Test {


	public void test() throws Throwable {
    test_session();
    test_response_root();
	}


  public void test_response_root() throws Throwable {
	  sub("Test response root");

    eq(NameCache.toNoneJavaName("TestWebContainer"),
            "test_web_container", "bad");

    msg("formatClassName xBoson --", NameCache.formatClassName(this.getClass()));
    msg("formatClassName Java   --", NameCache.formatClassName(String.class));
    msg("formatClassName Swing  --", NameCache.formatClassName(JFrame.class));
    msg("toNoneJavaName         --", NameCache.toNoneJavaName("TestWebContainer"));
  }


	public void test_session() throws Throwable {
		sub("Test session");
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
	}


	public static void main(String[] a) {
	  new TestWebContainer();
  }
}
