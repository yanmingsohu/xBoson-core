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
