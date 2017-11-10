/* CatfoOD 2017年11月2日 上午11:32:40 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import java.util.Random;

import com.xboson.util.AES;
import com.xboson.util.Tool;

public class TestAES extends Test {

	public void test() throws Throwable {
		String ps = "ccccc";
		byte[] key = AES.aesKey(ps);
		byte[] data = new byte[300 * 1024];
		msg("Data getLength:" + data.length);
		new Random().nextBytes(data);
		byte[] datax = AES.Encode(data, key);
		byte[] en = AES.Decode(datax, key);
		
		Tool.eq(en, data);
		success("AES ok");
	}

}
