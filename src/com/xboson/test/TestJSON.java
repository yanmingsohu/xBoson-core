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
import java.util.LinkedHashMap;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.xboson.been.ResponseRoot;
import com.xboson.j2ee.container.XResponse;
import com.xboson.util.OutputStreamSinkWarp;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;


public class TestJSON extends Test {


	public void test() throws Exception {
		test_arr();
		been_to_json();
		outputstream_warp();
		speed();
		thread_safe();
		map();
	}


	private static JsonAdapter<TestData> jsonAdapter;

	static {
		Moshi moshi = new Moshi.Builder().build();
		jsonAdapter = moshi.adapter(TestData.class);
	}


	private void test_arr() throws Exception {
	  sub("Array JSON to List");
    JsonAdapter<Object[]> ja = Tool.getAdapter(Object[].class);
    Object[] r = ja.fromJson("[1,null,true,\"abc\"]");
    msg(_string(r), r[0].getClass());
    eq(((Double)r[0]).intValue(), 1, "index 1");
    eq(r[1], null, "index 2");
    eq(r[2], true, "index 3");
    eq(r[3], "abc", "index 4");
  }


	public void map() {
	  sub("LinkedHashMap");
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put("a", 1);
    JsonAdapter ja = Tool.getAdapter(map.getClass());
    msg(ja.toJson(map));
    success("LinkedHashMap Worked !");
  }


	public void speed() {
	  sub("Speed Test");
		int count = 100000;
		TestData data = new TestData();

		{
			Moshi moshi = new Moshi.Builder().build();
			beginTime();
			for (int i=0; i<count; ++i) {
				jsonAdapter = moshi.adapter(TestData.class);
				jsonAdapter.toJson(data);
			}
			endTime("cache Moshi"); // 100000 Used Time 218ms
		}

		{
			Moshi moshi = new Moshi.Builder().build();
			jsonAdapter = moshi.adapter(TestData.class);
			beginTime();
			for (int i=0; i<count; ++i) {
				jsonAdapter.toJson(data);
			}
			endTime("cache adapter"); // 100000 Used Time 156ms
		}

		{
			beginTime();
			for (int i=0; i<count; ++i) {
				Moshi moshi = new Moshi.Builder().build();
				jsonAdapter = moshi.adapter(TestData.class);
				jsonAdapter.toJson(data);
			}
			endTime("All Function"); // 100000 Used Time 765ms
		}

		success("time test");
	}


	public void thread_safe() {
	  sub("Thread SAFE");
		final TestData data = new TestData();
		data.change();
		final Moshi moshi = new Moshi.Builder().build();
		final JsonAdapter<TestData> jsonAdapter2 = moshi.adapter(TestData.class);
		final String b = jsonAdapter2.toJson(data);

		final int count = 300000;
		final int threadc = 10;
		Thread t[] = new Thread[threadc];

		for (int c = 0; c<threadc; ++c) {
			t[c] = new Thread(new Runnable() {
				public void run() {
					for (int i=0; i<count; ++i) {
						jsonAdapter = moshi.adapter(TestData.class);
						String a = jsonAdapter.toJson(data);
						if (! a.equals(b)) {
							fail("bad value \n" + a + "\n" + b);
							System.exit(1);
						}
					}
					msg("Thread safe over " + Thread.currentThread().getId());
				}
			});
			t[c].start();
		}

		for (int c = 0; c<threadc; ++c) {
			try {
				t[c].join();
			} catch (InterruptedException e) {
				fail(c, t[c], "interrupted");
			}
		}
		success("thread safe");
	}


	public void been_to_json() throws IOException {
	  sub("Been to JSON");
		XResponse ret = new XResponse();

		TestData src = new TestData();
		src.change();
		ret.setData(src);
		msg(ret.toJSON());
		eq(ret.toJSON(), ret.toJSON(), "eq");

		success("been to JSON");
	}


	public void outputstream_warp() throws IOException {
	  sub("OutputStream wrap");
		TestData data = new TestData();
		data.change();

		OutputStream out = new StringBufferOutputStream();
		jsonAdapter.toJson(new OutputStreamSinkWarp(out), data);

		String a = out.toString();
		String b = jsonAdapter.toJson(data);

		TestData aa = jsonAdapter.fromJson(a);
		TestData bb = jsonAdapter.fromJson(b);

		msg(a);
		eq(aa, bb, "from json");
		eq(aa, data, "eq source");
	}


	public static void main(String[] s) throws Exception {
		new TestJSON();
	}
}