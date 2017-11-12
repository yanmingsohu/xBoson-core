////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月11日 09:05
// 原始文件路径: xBoson/src/com/xboson/test/TestEvent.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.event.GlobalEvent;
import com.xboson.event.GlobalListener;
import com.xboson.event.Names;
import com.xboson.event.QuickSender;

import javax.naming.Binding;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import java.util.Date;

public class TestEvent extends Test implements GlobalListener {
  private Object recv = null;
  private int count = 0;
  private boolean not_recive_when_off = true;


  static public class Data {
    public int a = (int)(Math.random() * 1000);
    public long b = (long)(Math.random() * 100000);
    public String name = Test.randomString(10);
  }


  public void test() throws Exception {
    GlobalEvent ge = GlobalEvent.me();
    ge.on("test", this);
    ge.on(Names.inner_error, this);

    send(new Date().toString());
    send("hello.2=" + Math.random());
    send(new Data());

    ok(recv != null, "recive data");
    ok(count == 3, "recive count");

    QuickSender.emitError(new Exception("yes"), this);

    not_recive_when_off = true;
    ge.off("test", null);
    ge.emit("test", "not recive");
    ok(not_recive_when_off, "off()");
  }


  void send(Object data) {
    GlobalEvent ge = GlobalEvent.me();
    ge.emit("test", data);
    eq(recv, data, "recive: " + recv);
  }


  @Override
  public void objectChanged(NamingEvent e) {
    Binding b = e.getNewBinding();
    String name = b.getName();
    Object data = b.getObject();
    recv = data;

//    msg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> [get event]"
//            + "\n\t\t\tname: "+ name
//            + "\n\t\t\tdata: " + data
//            + "\n\t\t\tinfo: " + e.getChangeInfo());

    if(name.equals(Names.inner_error) ) {
      throw (RuntimeException) b.getObject();
    }

    if ("not recive".equals(data)) {
      not_recive_when_off = false;
    }
    ++count;
  }


  @Override
  public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
  }


  public static void main(String[] a) throws Exception {
    unit("Global Event");
    new TestEvent();
  }
}
