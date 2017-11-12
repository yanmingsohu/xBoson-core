////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月1日 上午11:08:21
// 原始文件路径: xBoson/src/com/xboson/test/Test.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.*;

import com.xboson.init.Touch;
import com.xboson.log.LogFactory;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

/**
 * 通过实现该类, 导入通用测试框架
 */
public class Test {
	private static final String line =
    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";

	private static int failcount = 0;
	private static long time = 0;
	private static String unitname;
	private static boolean centralized = false;

	

	public static void main(String[] args) throws Throwable {
	  new Test(true);
	}


  public Test() {
	  if (centralized) return;
	  _test(new Test[] { this });
  }


  /**
   * 感知并运行 com.boson.test 包下所有测试用例
   * @param test_all
   * @throws Throwable
   */
  private Test(boolean test_all) throws Throwable {
    centralized = true;

    Set<Class> allclass = Tool.findPackage("com.xboson.test");
    Iterator<Class> it = allclass.iterator();
    Test[] test = new Test[allclass.size()];
    int i = 0;

    while (it.hasNext()) {
      Class c = it.next();
      test[i] = (Test) c.newInstance();
      ++i;
    }
    _test(test);
  }

	
	/**
	 * 子类重写该方法, 并且不要调用
	 */
	public void test() throws Throwable {
  }


	@SuppressWarnings("rawtypes")
	public final void _test(Test[] cl) {
    Touch.me();
    LogFactory.me().setType("TestOut");

		StringBufferOutputStream strerr = new StringBufferOutputStream();
		PrintStream buf = new PrintStream(strerr);

		for (int i=0; i<cl.length; ++i) {
			try {
				unit(cl[i].getClass().getName());
				Test t = cl[i];
				t.test();
				success();
			} catch(Throwable e) {
				fail(cl[i].getClass().getName());
				buf.println("\n" + line);
				buf.println("####\t" + cl[i].getClass().getName());
				buf.println(line);
				e.printStackTrace(buf);
			}
		}

		// 通知系统进入销毁流程
		Touch.exit();

		// 打印出积累的错误消息
		System.out.println("\u001b[;31m" + strerr + "\u001b[m");

		// 打印结果
		if (failcount > 0) {
			System.out.println("\n\u001b[;31m>>>>>>>>>> Over, Get " + failcount + " fail \u001b[m");
		} else {
			System.out.println("\n\u001b[;32m>>>>>>>>>> Over, All Passed \u001b[m");
		}
	}


	public static void success() {
	  success(unitname);
  }

	
	public static void success(Object o) {
		System.out.println("\u001b[;32m  Success: " + o + "\u001b[m");
	}
	
	
	public static void fail(Object o) {
		System.out.println("\u001b[;31m  Fail: " + o + "\u001b[m");
		++failcount;
	}


	/**
	 * 开始一条测试用例
	 */
	public static void unit(String name) {
		System.out.println("\u001b[;33m\nTest " + name + "\u001b[m");
    unitname = name;
	}


	/**
	 * 显示消息
	 */
	public static void msg(Object o) {
		System.out.println("\u001b[;36m    " + o + "\u001b[m");
	}


	/**
	 * 如果 o == false 则抛出异常
	 */
	public static void ok(boolean o, String msg) {
		if (!o) {
			throw new RuntimeException(msg);
		} else {
		  msg("OK " + msg);
    }
	}


	/**
	 * 如果 a, b 不相同则抛出异常
	 */
	public static void eq(Object a, Object b, String msg) {
	  if (a == b || a.equals(b)) {
	    msg("OK " + msg);
      return;
    }

	  throw new AssertionError(msg + " not equals\n\tObject: '" + a +
            "'\n\tObject: '" + b + "'");
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


	/**
	 * 低效的生成随机字符串, 仅用于测试
   *
	 * @deprecated 效率低
	 * @param byteLength 字节数量
	 * @return 字符串
	 */
	public static String randomString(int byteLength) {
		byte[] buf = randomBytes(byteLength);
		return Base64.getEncoder().encodeToString(buf);
	}


  /**
   * @deprecated 效率低
   */
	public static byte[] randomBytes(int byteLength) {
    byte[] buf = new byte[byteLength];
    Random r = new SecureRandom();
    r.nextBytes(buf);
    return buf;
  }


  public static void printArr(byte [] arr) {
	  msg(Arrays.toString(arr));
  }
}
