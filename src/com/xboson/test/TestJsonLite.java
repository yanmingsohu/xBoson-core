////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2021 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
