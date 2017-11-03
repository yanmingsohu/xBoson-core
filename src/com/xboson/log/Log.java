/* CatfoOD 2017年11月2日 下午5:18:45 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

public class Log {
	
	private Level cl = Level.ALL;
	private String name;
	
	
	Log(String name) {
		this.name = name;
	}
	

	public void logs(Level l, Object[] msg) {
		if (LogFactory.blocking(l) || cl.blocking(l))
			return;

		Date d = new Date();
		LogFactory.getLogWriter().output(d, l, name, msg);
	}
	
	
	/**
	 * 改变当前实例的日志级别
	 */
	public void setLevel(Level l) {
		l.checknull();
		cl = l;
	}
	
	
	public void log(Level l, Object... msg) {
		logs(l, msg);
	}
	
	public void info(Object... msg) {
		logs(Level.INFO, msg);
	}
	
	public void debug(Object... msg) {
		logs(Level.DEBUG, msg);
	}
	
	public void error(Object... msg) {
		logs(Level.ERR, msg);
	}
	
	public void warn(Object... msg) {
		logs(Level.WARN, msg);
	}
	
	public void fatal(Object... msg) {
		logs(Level.FATAL, msg);
	}
}
