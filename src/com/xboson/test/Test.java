/* CatfoOD 2017年11月1日 上午11:08:21 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import java.util.Date;

import com.xboson.log.LogFactory;

/**
 * 通过实现该类, 导入通用测试框架
 */
public class Test {
	
	private static int failcount = 0;
	private static long time = 0;
	private static String unitname;
	
	/**
	 * 测试用例列表
	 */
	@SuppressWarnings("rawtypes")
	private static final Class[] cl = new Class[] {
          TestAES.class,
          TestSession.class,
          TestJSON.class,
          TestUrl.class,
          TestTool.class,
          TestLog.class,
          TestScript.class,
          TestConfig.class,
          TestEvent.class,
	};
	

	public static void main(String[] args) throws Throwable {
		unit("Hello xBoson");
		new Test().test();
	}
	
	
	/**
	 * 子类重写该方法.
	 */
	@SuppressWarnings("rawtypes")
	public void test() throws Throwable {
		for (int i=0; i<cl.length; ++i) {
			try {
				unit(cl[i].getName());
				Class c = cl[i];
				Test t = (Test) c.newInstance();
				t.test();
			} catch(Error e) {
				fail(cl[i].getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}
		if (failcount > 0) {
			System.out.println("\n\u001b[;31m>>>>>>>>>> Over, Get " + failcount + " fail \u001b[m");
		} else {
			System.out.println("\n\u001b[;32m>>>>>>>>>> Over, All Passed \u001b[m");
		}
		new LogFactory.Init().contextDestroyed(null);;
	}

	
	public static void success(Object o) {
	  if (o == null) o = unitname;
		System.out.println("\u001b[;32m  Success: " + o + "\u001b[m");
	}
	
	
	public static void fail(Object o) {
		System.out.println("\u001b[;31m  Fail: " + o + "\u001b[m");
		++failcount;
	}
	
	
	public static void unit(String name) {
		System.out.println("\u001b[;33m\nTest " + name + "\u001b[m");
    unitname = name;
	}
	
	
	public static void msg(Object o) {
		System.out.println("\u001b[;36m    " + o + "\u001b[m");
	}
	
	
	public static void ok(boolean o, String msg) {
		if (!o) {
			throw new RuntimeException(msg);
		} else {
		  msg("OK " + msg);
    }
	}
	
	
	public static void memuse() {
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		msg("##### Heap utilization statistics [MB] #####");
		msg("  Used Memory:" 
			+ (runtime.totalMemory() - runtime.freeMemory()) / mb);
		msg("  Free Memory:" + runtime.freeMemory() / mb);
		msg("  Total Memory:" + runtime.totalMemory() / mb);
		msg("  Max Memory:" + runtime.maxMemory() / mb);
	}
	
	
	public static long beginTime() {
		return time = new Date().getTime();
	}
	
	
	public static void endTime(String msg) {
		long u = (new Date().getTime() - time); 
		msg(msg + " Used Time " + u + "ms");
	}
}
