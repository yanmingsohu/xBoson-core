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
// 文件创建日期: 7/24/21 10:43 AM
// 原始文件路径: /Users/mac/projects/xBoson/src/com/xboson/test/TestJsonLite.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.JsonLite;
import com.xboson.util.Tool;


public class TestJsonLite extends Test {

  public void test() throws Throwable {
    JsonLite j = JsonLite.objectRoot();
    j.put("es6", false);
    j.put("minify", true);
    j.end();
    Tool.pl(j);
  }


  public static void main(String[] a) {
    new TestJsonLite();
  }
}
