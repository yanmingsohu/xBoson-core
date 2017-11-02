/* CatfoOD 2017年11月2日 下午1:38:30 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;

public class TestJSON extends Test {
	
	// 不需要 get/set 就可以 tojson
	@SuppressWarnings("unused")
	private String a = "aaa";
	@SuppressWarnings("unused")
	private int i = 1;
	@SuppressWarnings("unused")
	private long l = 2;
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test() {

		Builder b = new Moshi.Builder();
		Moshi moshi = b.build();
		Class c = TestJSON.class;
		String s = moshi.adapter(c).toJson(this);
		msg("TO JSON: " + s);
		
		Object o = new TestJSON();
		msg( b.build().adapter(c).toJson(o) );
		
		success("JSON");
	}
}