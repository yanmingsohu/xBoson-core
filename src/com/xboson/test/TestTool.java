/* CatfoOD 2017年11月2日 下午4:23:19 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import com.xboson.util.Tool;

public class TestTool extends Test {

	public void test() {
		Exception e = create(20);
//		msg(Tool.allStack(e));
		msg(Tool.miniStack(e, 5));
		success("TestTool");
	}
	
	
	public Exception create(int i) {
		if (i > 0) {
			return create(--i);
		}
		return new Exception("Test Stack");
	}
}
