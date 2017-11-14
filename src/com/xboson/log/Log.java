////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午5:18:45
// 原始文件路径: xBoson/src/com/xboson/log/Log.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

import java.util.Date;

/**
 * 推荐使用非 static 变量存储 Log
 */
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
		LogFactory.changeLevel(this);
	}


	Level getLevel() {
		return cl;
	}


	String getName() {
		return name;
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
