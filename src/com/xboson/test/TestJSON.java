////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午1:38:30
// 原始文件路径: xBoson/src/com/xboson/test/TestJSON.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.xboson.been.ResponseRoot;
import com.xboson.util.JsonResponse;
import com.xboson.util.OutputStreamSinkWarp;
import com.xboson.util.StringBufferOutputStream;

public class TestJSON extends Test {
	
	// 不需要 get/set 就可以 tojson
	private String a = "aaa";
	private String[] aa = { "a", "fjdkslafjdsaklf", "你好" };
	private int i = 1000;
	private long l = 2;
	// private BigDecimal bd = new BigDecimal(-11); 不支持
	
	private A ia = new A();

	
	static public class A {
		String b = "a1";
		short sh = 1090;
		byte by = -128;
	}
	
	
	public boolean equals(TestJSON b) {
		return a.equals(b.a) && i == b.i && l == b.l && ia.b.equals(b.ia.b) 
				&& Arrays.equals(aa, b.aa);
	}
	
	
	private static JsonAdapter<TestJSON> jsonAdapter;
	
	static {
		Moshi moshi = new Moshi.Builder().build();
		jsonAdapter = moshi.adapter(TestJSON.class);
	}
	

	public void test() throws IOException {
		been_to_json();
		outputstream_warp();
		speed();
		thread_safe();
	}
	
	
	public void speed() {
		int count = 100000;
		
		{
			Moshi moshi = new Moshi.Builder().build();
			beginTime();
			for (int i=0; i<count; ++i) {
				jsonAdapter = moshi.adapter(TestJSON.class);
				jsonAdapter.toJson(this);
			}
			endTime("cache Moshi"); // 100000 Used Time 218ms
		}
		
		{
			Moshi moshi = new Moshi.Builder().build();
			jsonAdapter = moshi.adapter(TestJSON.class);
			beginTime();
			for (int i=0; i<count; ++i) {
				jsonAdapter.toJson(this);
			}
			endTime("cache adapter"); // 100000 Used Time 156ms
		}
		
		{
			beginTime();
			for (int i=0; i<count; ++i) {
				Moshi moshi = new Moshi.Builder().build();
				jsonAdapter = moshi.adapter(TestJSON.class);
				jsonAdapter.toJson(this);
			}
			endTime("All Function"); // 100000 Used Time 765ms
		}
		
		success("time test");
	}
	
	
	public void thread_safe() {
		final Moshi moshi = new Moshi.Builder().build();
		final JsonAdapter<TestJSON> jsonAdapter2 = moshi.adapter(TestJSON.class);
		final String b = jsonAdapter2.toJson(this);
		
		final int count = 300000;
		final int threadc = 10;
		Thread t[] = new Thread[threadc];
		
		for (int c = 0; c<threadc; ++c) {
			t[c] = new Thread(new Runnable() {
				public void run() {
					for (int i=0; i<count; ++i) {
						jsonAdapter = moshi.adapter(TestJSON.class);
						String a = jsonAdapter.toJson(TestJSON.this);
						if (! a.equals(b)) {
							fail("bad value \n" + a + "\n" + b);
							System.exit(1);
						}
					}
					msg("over");
				}
			});
			t[c].start();
		}
		
		for (int c = 0; c<threadc; ++c) {
			try {
				t[c].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		success("thread safe");
	}
	
	
	public void been_to_json() throws IOException {		
		JsonResponse ret = new JsonResponse();
		ResponseRoot root = ret.getRoot();
		root.setData(this);
		msg(ret.toString());
		
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<TestJSON> jsonAdapter = moshi.adapter(TestJSON.class);
		TestJSON r = jsonAdapter.fromJson(ret.toString());
		
		if (!r.equals(this)) {
			throw new IOException("json fail");
		}
		
		success("been to JSON");
	}
	
	
	public void outputstream_warp() throws IOException {
		OutputStream out = new StringBufferOutputStream();
		jsonAdapter.toJson(new OutputStreamSinkWarp(out), this);
		
		String a = out.toString();
		String b = jsonAdapter.toJson(this);
		
		TestJSON aa = jsonAdapter.fromJson(a);
		TestJSON bb = jsonAdapter.fromJson(b);
		
		msg(a);
		
		if (aa.equals(bb) && aa.equals(this)) {
			success("output stream warp");
		} else {
			throw new IOException("bad:\n" + a + "\n" + b);
		}
	}
	
	
	public static void main(String[] s) throws Exception {
		new TestJSON().test();
	}
}