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
// 文件创建日期: 17-11-12 下午5:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestUUID.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.script.lib.Uuid;

import java.util.UUID;


public class TestUUID extends Test {

  public void test() throws Throwable {
    check();
    generate1vs4();
    show();
  }


  public void check() {
    Uuid uid = new Uuid();
    final int count = 10000;

    beginTime();
    for (int i=0; i<count; ++i) {
      UUID src  = uid.v4obj();
      String ds = uid.ds(src);
      UUID pds  = uid.parseDS(ds);
      String z  = uid.zip(src);
      UUID uz   = uid.unzip(z);

      eq(ds.length(), 32, "ds length 32");
      eq(pds, src, "parse ds");
      eq(uz, src, "zip uuid");
    }
    endTime("Generate ds/parse ds/zip/unzip UUID", count, "counts");
  }


  public void show() {
    Uuid uid = new Uuid();

    sub("v1()");
    for (int i=0; i<5; ++i) {
      msg("v1: " + uid.v1());
    }

    sub("v4()");
    for (int i=0; i<5; ++i) {
      msg("v4: " + uid.v4());
    }


    UUID id = uid.v4obj();
    String ds = uid.ds(id);
    UUID pds = uid.parseDS(ds);
    String z = uid.zip(id);
    UUID uz = uid.unzip(z);

    sub("strings:");
    msg("UUID : ", id, "[ version:", id.version(),
            "length:", id.toString().length(), "]");
    msg("DS   : ", ds, "[ length:", ds.length(), "]");
    msg("PDS  : ", pds);
    msg("ZIP  : ", z, "[ length:", z.length(), "]");
    msg("UNZIP: ", uz);

//    msg(id.timestamp(), id.variant(), id.version());
  }


  public void generate1vs4() {
    Uuid uid = new Uuid();
    final int count = 100000;

    beginTime();
    for (int i=count; i>=0; --i) {
      uid.v1();
    }
    endTime("Generate v1 UUID", count, "counts");

    beginTime();
    for (int i=count; i>=0; --i) {
      uid.v4();
    }
    endTime("Generate v4 UUID", count, "counts");
  }

  public static void main(String[] a) {
    new TestUUID();
  }
}
