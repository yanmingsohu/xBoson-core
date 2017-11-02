/* CatfoOD 2017年11月1日 上午11:08:21 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;


/**
 * 通过实现该类, 导入通用测试框架
 */
public class Test {
	
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
			} catch(Exception e) {
				fail(cl[i].getName() + " " + e.getMessage());
			}
		}
	}

	
	public static void success(Object o) {
		System.out.println("\u001b[;32m  Success: " + o + "\u001b[m");
	}
	
	
	public static void fail(Object o) {
		System.out.println("\u001b[;31m  Fail: " + o + "\u001b[m");
	}
	
	
	public static void unit(String name) {
		System.out.println("\u001b[;33m\nTest " + name + "\u001b[m");
	}
	
	
	public static void msg(Object o) {
		System.out.println("\u001b[;36m    " + o + "\u001b[m");
	}
	
	
	public static void ok(boolean o, String msg) {
		if (o) {
			success("ok");
		} else {
			success("fail: " + msg);
		}
	}
}
