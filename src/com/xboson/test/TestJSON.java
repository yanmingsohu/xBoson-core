/* CatfoOD 2017年11月2日 下午1:38:30 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import java.io.IOException;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.xboson.been.ResponseRoot;
import com.xboson.util.JsonResponse;

public class TestJSON extends Test {
	
	// 不需要 get/set 就可以 tojson
	private String a = "aaa";
	private int i = 1;
	private long l = 2;
	
	private A ia = new A();
	

	public void test() throws IOException {
		unit("Been to JSON");
		
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
		
		success("JSON");
	}
	
	
	static public class A {
		String b = "a1";
	}
	
	
	public boolean equals(TestJSON b) {
		return a == b.a && i == b.i && l == b.l && ia.b == b.ia.b;
	}
}