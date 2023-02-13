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
// 文件创建日期: 17-12-12 下午7:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestReaderSet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.ReaderSet;

import java.util.Arrays;


public class TestReaderSet extends Test {

  @Override
  public void test() throws Throwable {
    ReaderSet rs = new ReaderSet();
    rs.add("abcdef");
    rs.add("123456789".getBytes());

    char[] buf = new char[10];
    rs.read(buf, 0, 10);
    msg(Arrays.toString(buf));

    rs.reset();

    rs.read(buf, 2, 8);
    msg(Arrays.toString(buf));
  }


  public static void main(String[] a) {
    new TestReaderSet();
  }

}
