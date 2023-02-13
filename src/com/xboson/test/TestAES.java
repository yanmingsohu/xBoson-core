/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 2017年11月2日 上午11:32:40
// 原始文件路径: xBoson/src/com/xboson/test/TestAES.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import java.util.Random;

import com.xboson.app.ApiEncryption;
import com.xboson.util.AES;
import com.xboson.util.Tool;
import com.xboson.util.c0nst.IConstant;


public class TestAES extends Test {

	public void test() throws Throwable {
		test1();
		testApi();
	}

	private void testApi() throws Exception {
		sub("Test ApiEncryption.encryptApi2");
		String a = "function test() { console.log('ok'); }";
		String b = ApiEncryption.me.encryptApi2(a, 1);
		String c = new String(ApiEncryption.me.decryptApi2(b, 1), IConstant.CHARSET);
		eq(a, c, "encrypt api 2");
		msg("2:", b);
		msg("1:", ApiEncryption.encryptApi(a));
	}

	private void test1() throws Throwable {
		String ps = "ccccc";
		byte[] key = AES.aesKey(ps);
		byte[] data = new byte[300 * 1024];
		msg("Data getLength:" + data.length);
		new Random().nextBytes(data);
		byte[] datax = AES.Encode(data, key);
		byte[] en = AES.Decode(datax, key);
		
		Tool.eq(en, data);
	}

	public static void main(String[] ar) throws Throwable {
		new TestAES().test();
	}

}
