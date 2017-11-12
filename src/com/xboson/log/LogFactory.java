////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午5:19:09
// 原始文件路径: xBoson/src/com/xboson/log/LogFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

import com.xboson.been.Config;
import com.xboson.event.OnExitHandle;
import com.xboson.util.SysConfig;


public class LogFactory extends OnExitHandle {

	private static LogFactory instance;
	private static Level level;
	private static ILogWriter writer;
	
	
	static {
		setLevel(Level.ALL);
		/** 仅在启动期间保持日志, 启动后立即切换 */
		writer = new SavedOut();
	}

	public synchronized static LogFactory me() {
		if (instance == null) {
			instance = new LogFactory();
		}
		return instance;
	}
	
	
	private LogFactory() {
		try {
			Config cfg = SysConfig.me().readConfig();
			setType(cfg.loggerWriterType);
			setLevel(Level.find(cfg.logLevel));

			create().info("Initialization Success, Log level", level);
		} finally {
			if (writer == null) {
				writer = new ConsoleOut();
			}
		}
	}


	@Override
	protected void exit() {
		if (writer != null) {
			writer.destroy(null);
			// 保证在退出后仍然可用
			writer = new ConsoleOut();
		}
	}
	

	/**
	 * 创建日志实例, 用于输出日志.
	 */
	public static Log create(String name) {
		return new Log(name);
	}
	
	
	/**
	 * 创建日志实例, 用于输出日志, 类名作为日志名.
	 */
	public static Log create(Class<?> c) {
		return new Log(c.getName());
	}
	
	
	/**
	 * 使用调用该方法的类名作为日志名, 在集成系统中, 始终使用父类的名称.
	 */
	public static Log create() {
		Exception e = new Exception();
		StackTraceElement[] t = e.getStackTrace();
		return create(t[1].getClassName());
	}
	
	
	/**
	 * 设置当前日志级别
	 */
	public static void setLevel(Level l) {
		l.checknull();
		level = l;
	}
	
	
	static ILogWriter getLogWriter() {
		return writer;
	}
	
	
	static boolean blocking(Level l) {
		return level.blocking(l);
	}


	public synchronized boolean setType(String type) {
		try {
			Class<?> cl = Class.forName("com.xboson.log." + type);
			ILogWriter older = writer;
			writer = (ILogWriter) cl.newInstance();
			older.destroy(writer);
			return true;
		} catch(Exception e) {
			System.out.println("Init log fail: " + e.getMessage());
		}
		return false;
	}
}
