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
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;

import com.xboson.been.JsonHelper;
import com.xboson.init.Touch;
import com.xboson.log.LogFactory;
import com.xboson.sleep.ISleepwalker;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;

/**
 * 通过实现该类, 导入通用测试框架,
 * 这里的方法都没有考虑性能, 不要再非测试环境中使用.
 */
public class Test {
	public static final String line =
">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";

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

    printRunningThread();
	}


	public static void success() {
	  success(unitname);
  }

	
	public static void success(Object ...o) {
		System.out.println("\u001b[;32m  Success: " + _string(o) + "\u001b[m");
	}
	
	
	public static void fail(Object ...o) {
		red("  Fail: " + _string(o));
		++failcount;
	}


	public static void red(Object ...o) {
    System.out.println("\u001b[;31m" + _string(o) + "\u001b[m");
  }


	/**
	 * 开始一条测试用例
	 */
	private static void unit(String name) {
		System.out.println("\u001b[;33m\nTest " + name + "\u001b[m");
    unitname = name;
	}


  /**
   * 一个测试用例中的子项测试
   * @param msg
   */
	public static void sub(Object ...msg) {
    System.out.println("\u001b[;35m  " + _string(msg) + "\u001b[m");
  }


	/**
	 * 显示消息
	 */
	public static void msg(Object ...o) {
		System.out.println("\u001b[;36m    " + _string(o) + "\u001b[m");
	}


  public static String _string(Object [] arr) {
    if (arr == null) return "";
    if (arr.length == 1) return arr[0].toString();

    StringBuilder out = new StringBuilder();
    for (int i=0; i<arr.length; ++i) {
      out.append(arr[i]);
      out.append(' ');
    }
    return out.toString();
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
      return;
    }

	  throw new AssertionError(msg + " not equals\n\tObject: '" + a +
            "'\n\tObject: '" + b + "'");
  }


  /**
   * 显示内存状态
   */
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


  /**
   * 设定开始时间
   */
	public static long beginTime() {
		return time = new Date().getTime();
	}


  /**
   * 用设定的开始时间和调用此函数的结束时间, 计算使用时间
   */
	public static long endTime(Object ...msg) {
		long u = (new Date().getTime() - time); 
		sub(_string(msg), "Used Time", u, "ms");
		return u;
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
		return Base64.getEncoder().withoutPadding().encodeToString(buf);
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



  static public abstract class TData extends JsonHelper
					implements ISleepwalker, Serializable {
    public int a = 0;
    public int b = 0;
    public long c = 0;
    public String d = "not_init";
    public String id = "not_init_id";

    public void change() {
      a = (int) (Math.random() * 100);
      b = (int) (Math.random() * 1000 + 100);
      c = (int) (Math.random() * 10000 + 1000);
      d = Test.randomString(100);
    }

    public boolean equals(Object _o) {
      if (_o instanceof TData) {
        TData o = (TData) _o;
        return a == o.a && b == o.b && c == o.c
                && d.equals(o.d);
      }
      return false;
    }

    public String toString() {
      return "[ a=" + a + " b=" + b + " c=" + c + " d=" + d + " ]";
    }
  }


  /**
   * 专门用来测试 JSON 和序列化的数据对象
   */
  static public class TestData extends TData {
    @Override
    public String getid() {
      return "null";
    }
  }


  /**
   * 打印非守护线程的堆栈
   */
  static void printRunningThread() {
    Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
    Iterator<Thread> it = all.keySet().iterator();
    Thread myself = Thread.currentThread();
    int activeCount = 0;

    StringBufferOutputStream strbuf = new StringBufferOutputStream();
    PrintStream buf = new PrintStream(strbuf);

    while (it.hasNext()) {
      Thread t = it.next();
      buf.println("\u001b[;33m\nThread: " + t + "\u001b[m");

      if (t.getThreadGroup().getName().equals("system") == false
              && t.isDaemon() == false && t != myself) {
        StackTraceElement[] ste = all.get(t);
        for (int i = 0; i < ste.length; ++i) {
          buf.println("\u001b[;31m\t" + ste[i] + "\u001b[m");
          ++activeCount;
        }
      } else{
        buf.println("\tSystem or Daemon Thread.");
      }
    }

    if (activeCount > 0) {
      System.out.println("Running Thread: " + activeCount);
      System.out.println(strbuf);
    }
  }


  /**
   * 抛出异常才认为是正确的行为
   */
  static public abstract class Throws {
    /**
     * 正确运行时抛出 _throws 类型的异常
     */
    public Throws(Class<? extends Throwable>  _throws) {
      try {
        run();
      } catch(Throwable t) {
        if (_throws.isAssignableFrom(t.getClass())) {
          msg("OK, cache", t);
          return;
        }
      }
      throw new RuntimeException("cannot throw Throwable: " + _throws);
    }
    public abstract void run() throws Throwable;
  }
}
