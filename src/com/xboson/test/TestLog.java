/* CatfoOD 2017年11月2日 下午5:39:34 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import com.xboson.log.Level;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

public class TestLog extends Test {

	public void test() throws Exception {
		LogFactory.Init init = new LogFactory.Init();
		
		Log log = LogFactory.create();
		log.debug("display debug", 1);
		log.info("display info", 2);
		log.error("display error", 3);
		log.warn("display", "warn", 4);
		log.fatal("display", "fatal", 5);

		init.setType("FileAndConsoleOut");
		
		LogFactory.setLevel(Level.ERR);
		log.debug("!!! not display", 11);
		log.error("display when set level", 6);
		
		log.setLevel(Level.FATAL);
		log.error("!!! not display", 12);
		log.fatal("display when change level", 7);
		
		LogFactory.setLevel(Level.ALL);
		success("log");
	}
}
