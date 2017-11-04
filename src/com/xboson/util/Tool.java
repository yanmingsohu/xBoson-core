/* CatfoOD 2017年11月2日 上午8:46:25 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
	
	private static final ThreadLocal<SimpleDateFormat> 
					dataformat = new ThreadLocal<SimpleDateFormat>();
	
	private Tool() {}
	
	
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
	
	
	public static void close(InputStream i) {
		try {
			if (i != null) i.close();
		} catch(Exception e) {}
	}
	
	
	public static void close(OutputStream i) {
		try {
			if (i != null) i.close();
		} catch(Exception e) {}
	}
	
	
	public static void close(Writer w) {
		try {
			if (w != null) w.close();
		} catch(Exception e) {
		}
	}
	
	
	public static void close(Reader w) {
		try {
			if (w != null) w.close();
		} catch(Exception e) {
		}
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
}
