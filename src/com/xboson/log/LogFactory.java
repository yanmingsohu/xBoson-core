/* CatfoOD 2017年11月2日 下午5:19:09 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class LogFactory {
	
	private static Level level;
	private static ILogWriter writer;
	
	
	static {
		setLevel(Level.ALL);
		writer = new SavedOut();
	}
	
	
	private LogFactory() {}
	

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
	
	
	static public class Init implements ServletContextListener {
		
		public void contextDestroyed(ServletContextEvent sce) {
			if (writer != null) {
				writer.destroy(null);
				writer = null;
			}
		}
		
		public void contextInitialized(ServletContextEvent sce) {
			try {
				String type = sce.getServletContext().getInitParameter("LoggerWriter");
				setType(type);
			} catch(Exception e) {
				System.out.println("Init log fail:");
				e.printStackTrace();
			} finally {
				if (writer == null) {
					writer = new ConsoleOut();
				}
			}
		}
		
		public void setType(String type) throws 
				ClassNotFoundException, 
				InstantiationException, 
				IllegalAccessException {
			Class<?> cl = Class.forName("com.xboson.log." + type);
			ILogWriter older = writer;
			writer = (ILogWriter) cl.newInstance();
			older.destroy(writer);
		}
	}
}
