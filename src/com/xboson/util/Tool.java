////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 上午8:46:25
// 原始文件路径: xBoson/src/com/xboson/util/Tool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;

public class Tool {
	
	private static final ThreadLocal<SimpleDateFormat> 
					dataformat = new ThreadLocal<SimpleDateFormat>();
	
	private static Builder jsbuilded;
	private static Moshi moshi;
	
	private Tool() {}
	
	static {
		jsbuilded = new Moshi.Builder();
		moshi = jsbuilded.build();
	}


	/**
	 * 保证性能和线程安全; 返回的适配器可以缓存, 性能更好;
	 */
	public static <E> JsonAdapter<E> getAdapter(Class<E> c) {
		return moshi.adapter(c);
	}
	
	
	public static void regJsonAdapter(Object adapter) {
		jsbuilded.add(adapter);
	}
	
	
	public static void eq(byte[] a, byte[] b) {
		if (a.length != b.length)
			throw new RuntimeException("bad length " + a.length + " != " + b.length);
		
		for (int i=0; i<a.length; ++i) {
			if (a[i] != b[i]) {
				throw new RuntimeException("On " + i + " a=" + a[i] + " b=" + b[i] + " not eq");
			}
		}
	}
	
	
	public static void println(byte[] a) {
		for (int i=0; i<a.length; ++i) {
			System.out.print(Integer.toString(a[i], 16) + " " );
			if (i % 16 == 0) System.out.println();
		}
		System.out.println();
	}
	
	
	/**
	 * 返回完整堆栈字符串
	 */
	public static String allStack(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);
		e.printStackTrace(ps);
		ps.flush();
		return sw.toString();
	}
	
	
	/**
	 * 返回部分堆栈字符串, 包含至多 count 个堆栈
	 */
	public static String miniStack(Throwable e, int count) {
		StringBuilder buff = new StringBuilder();
		StackTraceElement[] trace = e.getStackTrace();
		if (count <= 0) {
			count = trace.length;
		} else {
			count = Math.min(count, trace.length);
		}
		
		buff.append(e.getClass());
		buff.append(": ");
		buff.append(e.getMessage());
		
		for (int i=0; i<count; ++i) {
			buff.append("\r\n\t");
			buff.append(trace[i].getClassName());
			buff.append("->");
			buff.append(trace[i].getMethodName());
			buff.append("() [");
			buff.append(trace[i].getFileName());
			buff.append(" :");
			buff.append(trace[i].getLineNumber());
			buff.append("]");
		}
		
		if (count < trace.length) {
			buff.append("\r\n\t... ");
			buff.append(trace.length - count);
		}
		
		return buff.toString();
	}
	
	
	/**
	 * 优化性能, 总是返回完整的日期/时间字符串
	 */
	public static String formatDate(Date d) {
		SimpleDateFormat f = dataformat.get();
		if (f == null) {
			f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			dataformat.set(f);
		}
		return f.format(d);
	}
	
	
	/**
	 * 复制并关闭文件
	 * @throws IOException 
	 */
	public static void copy(InputStream src, OutputStream dst, boolean close) throws IOException {
		try {
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = src.read(buff)) > 0) {
				dst.write(buff, 0, len);
			}
		} finally {
			if (close) {
				close(src);
				close(dst);
			}
		}
	}
	
	
	public static StringBuilder readFromFile(String filename) throws IOException {
		FileReader r = null;
		try {
			StringBuilder out = new StringBuilder();
			r = new FileReader(filename);
			char[] buff = new char[1024];
			int len = 0;
			while ((len = r.read(buff)) > 0) {
				out.append(buff, 0, len);
			}
			return out;
		} finally {
			close(r);
		}
	}


	public static void close(Closeable c) {
	  try {
	    if (c != null) c.close();
    } catch(Exception e) {}
  }


  /**
   * 等待线程结束后返回
   */
  public static void waitOver(Thread t) {
	  try {
      if (t != null) t.join();
    } catch(Exception e) {}
  }
	
	
	public static short reverseBytesShort(short s) {
    int i = s & 0xffff;
    int reversed = (i & 0xff00) >>> 8
        |          (i & 0x00ff)  << 8;
    return (short) reversed;
  }
	

  public static int reverseBytesInt(int i) {
    return (i & 0xff000000) >>> 24
        |  (i & 0x00ff0000) >>>  8
        |  (i & 0x0000ff00)  <<  8
        |  (i & 0x000000ff)  << 24;
  }

  
  public static long reverseBytesLong(long v) {
    return (v & 0xff00000000000000L) >>> 56
        |  (v & 0x00ff000000000000L) >>> 40
        |  (v & 0x0000ff0000000000L) >>> 24
        |  (v & 0x000000ff00000000L) >>>  8
        |  (v & 0x00000000ff000000L)  <<  8
        |  (v & 0x0000000000ff0000L)  << 24
        |  (v & 0x000000000000ff00L)  << 40
        |  (v & 0x00000000000000ffL)  << 56;
  }


  /**
   * 获取对象数组中每个对象的 class
   * @param args 当函数返回, 元素可能被 convert 改变
   * @param convert 可以 null, 对每个数组元素做数据转换和类型转换
   * @return 返回经过转换的 class 数组
   */
  public static Class<?>[] getClasses(
          Object[] args, IConversion<Object, Object> convert) {
    Class<?>[] classs = new Class<?>[args.length];

    for (int i=0; i<classs.length; ++i) {
      Object arg = args[i];

      if (convert != null) {
        Object old = arg;
        arg        = convert.value(arg);
        classs[i]  = convert.type(old.getClass());
        args[i]    = arg;
      } else {
        classs[i] = arg.getClass();
      }
    }
    return classs;
  }

	/**
	 * 返回 i 做为无符号字节的 16 进制字符串
	 */
  public static String ubytehex(byte i) {
		int ii = Byte.toUnsignedInt(i);
		return "" + hex(ii >> 4) + hex(ii);
	}


	/**
	 * 将 (i & 0x0F) 的值转换为 16 进制字符
	 */
	public static char hex(Number i) {
  	int b = 0x0F & i.intValue();
  	if (b >= 0) {
      if (b < 10) {
        return (char) ((int) '0' + b);
      }
      if (b < 16) {
        return (char) ((int) 'A' + (b-10));
      }
    }
		throw new IllegalArgumentException("number on 0-15");
	}


	/**
	 * 返回字符串首字母大写的形式
	 */
	public static String upperFirst(String n) {
		return Character.toUpperCase(n.charAt(0)) + n.substring(1);
	}


	/**
	 * 获取包下的所有类类型, 排除子包, 排除内部类,
   *
   * @deprecated 不推荐在正式代码中使用, 可用于测试
	 * @param packageName 包名
	 * @return 即使找不到类, 也会返回空数组
	 */
	public static Set<Class> findPackage(String packageName)
          throws IOException, ClassNotFoundException {
    Package pk = Package.getPackage(packageName);
    if (pk == null)
      throw new RuntimeException("cannot find package: " + packageName);

    String packagePath = packageName.replaceAll("\\.", "/");
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(packagePath);

    String fullPath = url.getPath();
    File[] files = new File(fullPath).listFiles();
    int prefixLength = fullPath.length() - packageName.length() - 1;

    Set<Class> ret = new HashSet<>();

    for (int i=0; i<files.length; ++i) {
      File file = files[i];
      String name = file.toString();
      name = name.substring(prefixLength);

      if (name.lastIndexOf(".class") == name.length() - 6) {
        if (name.indexOf('$') >= 0) continue;
        name = name.substring(0, name.length() - 6);
        name = name.replaceAll("/|\\\\", ".");
        ret.add(Class.forName(name));
      }
    }

    return ret;
	}


  /**
   * @see java.lang.Thread#sleep(long)
   */
	public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch(Exception e) {
      System.err.println(e);
    }
  }

}
