/* CatfoOD 2017年11月2日 下午1:38:30 yanming-sohu@sohu.com Q.412475540 */

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
	private int i = 1;
	private long l = 2;
	
	private A ia = new A();

	
	static public class A {
		String b = "a1";
		short sh = 1090;
		byte by = -128;
	}
	
	
	public boolean equals(TestJSON b) {
		return a.equals(b.a) && i == b.i && l == b.l && ia.b.equals(b.ia.b) && Arrays.equals(aa, b.aa);
	}
	
	
	private static JsonAdapter<TestJSON> jsonAdapter;
	
	static {
		Moshi moshi = new Moshi.Builder().build();
		jsonAdapter = moshi.adapter(TestJSON.class);
	}
	

	public void test() throws IOException {
		been_to_json();
		outputstream_warp();
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
		
		if (!aa.equals(bb)) {
			throw new IOException("bad:\n" + a + "\n" + b);
		}

		success("output stream warp");
	}
	
}