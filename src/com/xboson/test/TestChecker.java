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
// 文件创建日期: 17-11-13 下午5:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestChecker.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.script.lib.Checker;
import com.xboson.script.lib.Uuid;

public class TestChecker extends Test {

  public void test() throws Exception {
    Checker c = new Checker();
    Uuid u = new Uuid();


    sub("Test uuid checker");
    beginTime();
    for (int i=0; i<1000; ++i) {
      c.uuid(u.v1(), "uuid bad");
    }
    endTime("uuid checked");


    sub("Test base64 checker");
    beginTime();
    for (int i=0; i<1000; ++i) {
      c.base64url(u.zip(), "base64 url bad");
    }
    endTime("base64 url checked");


    new Throws(Checker.CheckException.class) {
      public void run() throws Throwable {
        c.safepath("/a/b/c/\\../c", "good");
      }
    };
  }


  public static void main(String[] a) {
    new TestChecker();
  }

}
