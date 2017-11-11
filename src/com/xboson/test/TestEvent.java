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

import com.xboson.util.GlobalEvent;

import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;

public class TestEvent extends Test implements GlobalEvent.GlobalListener {
  private Object recv = null;
  private int count = 0;

  public void test() throws Exception {
    GlobalEvent ge = GlobalEvent.me();
    ge.on("test", this);

    ge.emit("test", "hello");
    ge.emit("test", "hello1");
    ge.emit("test", "hello2");

    ok(recv != null, "recive data");
    ok(count == 3, "recive count");
    success(null);
  }

  @Override
  public void objectChanged(NamingEvent e) {
    recv = e.getNewBinding();
    msg("[get event] "+ recv);
    ++count;
  }

  @Override
  public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
  }

  public static void main(String[] a) throws Exception {
    unit("Global Event");
    new TestEvent().test();
  }
}
