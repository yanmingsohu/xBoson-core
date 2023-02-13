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
// 文件创建日期: 2017年11月2日 下午3:05:53
// 原始文件路径: xBoson/src/com/xboson/test/TestUrl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.UrlSplit;
import com.xboson.script.lib.Path;


public class TestUrl extends Test {

  final String a = "/xboson";
  final String b = "/app/{app id}/{org id}/{module id}/{api name}";


	public void test() throws Exception {
    testPath();
    test1();
    test2();
	}


	public void testPath() {
	  sub("Test Path");
	  Path p = Path.me;

	  eq(p.resolve(""), "/", "resolve");

    eq(p.normalize("/foo/bar//baz/asdf/quux/.."),
            "/foo/bar/baz/asdf", "normalize1");
    eq(p.normalize("C:\\\\temp\\\\\\\\foo\\\\bar\\\\..\\\\"),
            "C:/temp/foo", "normalize2");
    eq(p.normalize("C:////temp\\\\\\\\/\\\\/\\\\/foo/bar"),
            "C:/temp/foo/bar", "normalize3");
    eq(p.normalize("/c/../a/b"), "/a/b", "normalize4");
    eq(p.normalize("c/../a/b"), "a/b", "normalize5");
    eq(p.normalize("../../a/b"), "../../a/b", "normalize6");
    eq(p.normalize("/a"), "/a", "normalize7");
    eq(p.normalize("/"), "/", "normalize8");
    eq(p.normalize(""), "", "normalize9");
    eq(p.normalize("////"), "/", "normalize10");

	  String a = "/data/orandea/test/aaa";
	  String b = "/data/orandea/impl/bbb";
	  String c = "../../impl/bbb";
	  eq(p.relative(a, b), c, "path.relative");
  }


	public void test2() {
	  sub("Test url without slash");

    UrlSplit url = new UrlSplit("/a/b/c");
    msg("All:", url);
    url.withoutSlash(true);
    msg("without slash:", url);

    eq(url.getName(), "a", "bad");
    eq(url.next(), "b", "bad");
    eq(url.next(), "c", "bad");

    new Throws(UrlSplit.URLParseException.class) {
      @Override
      public void run() throws Throwable {
        url.next();
      }
    };
  }


	public void test1() {
	  sub("Test url normal");

    UrlSplit url = new UrlSplit(a + b);
    msg("All:", url);

    eq(url.getName(), a, "name bad");
    eq(url.getLast(), b, "last bad");

    String n = url.next();
    eq(n, "/app", "next fail");
    msg("next:", n);

    n = url.next();
    msg("next:", n);

    n = url.next();
    eq(n, "/{org id}", "next fail");
    msg("next:", n);

    n = url.next();
    msg("next:", n);

    n = url.next();
    msg("next:", n);

    new Throws(UrlSplit.URLParseException.class) {
      @Override
      public void run() throws Throwable {
        url.next();
      }
    };
  }


	public static void main(String[] a) {
		new TestUrl();
	}
}
