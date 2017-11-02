/* CatfoOD 2017年11月2日 上午8:46:25 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
	
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
	
	
	public static String formatDate(Date d) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return f.format(d);
	}
}
