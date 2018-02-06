////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-2-5 下午6:13
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestSign.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.xboson.been.License;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.config.YamlConfigImpl;


public class TestSign extends Test {

  @Override
  public void test() throws Throwable {
    write();
  }


  private void write() throws Exception {
    sub("Generate License Data");
    License li = new License();
    li.appName    = "大数据平台内核";
    li.company    = "内测";
    li.dns        = "www.xboson.x.y.z";
    li.email      = "yanmingsohu@live.com";
    li.beginTime  = System.currentTimeMillis();
    li.endTime    = Long.MAX_VALUE;
    li.init();

    YamlConfig yc = YamlConfigImpl.basicConfig();
    yc.setClassTag("License", li.getClass());
    StringBufferOutputStream buf = new StringBufferOutputStream();
    YamlWriter w = new YamlWriter(buf.openWrite(), yc);
    w.write(li);
    w.close();

    sub("Signature");
    msg(buf.toString());

    msg("OK");
  }


  public static void main(String[] a) {
    new TestSign();
  }

}
