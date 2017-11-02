/* CatfoOD 2017年11月2日 下午3:05:53 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import com.xboson.been.UrlSplit;

public class TestUrl extends Test {

	public void test() throws Exception {
		String a = "/xboson";
		String b = "/api/a/b/c";
		UrlSplit url = new UrlSplit(a + b);
		msg(url);
		
		if (!url.getName().equals(a))
			throw new Exception("name bad");
		
		if (!url.getLast().equals(b))
			throw new Exception("last bad");
			
		success("URL");
	}
}
