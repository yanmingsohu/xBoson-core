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
// 文件创建日期: 2017年11月2日 下午5:39:34
// 原始文件路径: xBoson/src/com/xboson/test/TestLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.log.Level;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.log.writer.FileAndConsoleOut;
import com.xboson.log.writer.TestOut;


public class TestLog extends Test {

	public void test() throws Exception {
		LogFactory lf = LogFactory.me();
		msg("This is SHOW: 1,2,3,4,5,6,7");
		
		Log log = LogFactory.create();
		log.setLevel(Level.ALL);
		log.debug("display debug", 1);
		log.info("display info", 2);
		log.error("display error", 3);
		log.warn("display", "warn", 4);
		log.fatal("display", "fatal", 5);

		lf.setWriter(new FileAndConsoleOut());

    log.setLevel(Level.INHERIT);
		LogFactory.setLevel(Level.ERR);
		log.debug("!!! not display", 11);
		log.error("display when set level", 6);
		
		log.setLevel(Level.FATAL);
		log.error("!!! not display", 12);
		log.fatal("display when change level", 7);
		
		LogFactory.setLevel(Level.ALL);

		lf.setWriter(new TestOut());
	}


	public static void main(String[] s) {
	  new TestLog();
  }
}
