/* CatfoOD 2017年11月5日 上午11:50:29 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script.lib;

import com.xboson.log.Level;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.JSObject;


public class Console extends JSObject {
	
	private Log log;


	public Console() {
		log = LogFactory.create("script.console");
	}


	public Console(String name) {
		log = LogFactory.create(name);
	}


	public Console create(String name) {
		return new Console(name);
	}

	
	@Override
	public String env_name() {
		return "console";
	}

	
	public Console info(Object ...msg) {
		log.logs(Level.INFO, msg);
		return this;
	}
	
	
	public Console log(Object ...msg) {
		log.logs(Level.INFO, msg);
		return this;
	}
	
	
	public Console debug(Object ...msg) {
		log.logs(Level.DEBUG, msg);
		return this;
	}
	
	
	public Console error(Object ...msg) {
		log.logs(Level.ERR, msg);
		return this;
	}
	
	
	public Console warn(Object ...msg) {
		log.logs(Level.WARN, msg);
		return this;
	}
	
	
	public Console fatal(Object ...msg) {
		log.logs(Level.FATAL, msg);
		return this;
	}


	public Console trace(Object ...msg) {
		return debug(msg);
	}
}
