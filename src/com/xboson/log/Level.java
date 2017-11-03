/* CatfoOD 2017年11月2日 下午5:19:38 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;


public class Level {

	public static final Level DEBUG	= new Level("DEBUG",  5);
	public static final Level INFO	= new Level(" INFO", 10);
	public static final Level WARN  = new Level(" WARN", 15);
	public static final Level ERR		= new Level("ERROR", 20);
	public static final Level FATAL	= new Level("FATAL", 25);
	
	public static final Level ALL		= new Level("ALL",    0);
	public static final Level OFF		= new Level("OFF", 9999);

	
	private Level(String l, int n) {
		str = l;
		num = n;
	}
	
	
	public String toString() {
		return str;
	}
	
	
	/**
	 * this - 当前日志级别
	 * @param l - 要检测的日志级别
	 * @return 允许在当前日志级别显示 l 的日志返回 true
	 */
	public boolean blocking(Level l) {
		return num > l.num;
	}
	
	
	public void checknull() {
	}
	
	
	private int num;
	private String str;
}
