////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-11 下午12:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestSleep.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.sleep.*;

import java.io.Serializable;


public class TestSleep extends Test {


  public void test() {
    {
      RedisMesmerizer rm = RedisMesmerizer.me();
      SleepFactory sf = SleepFactory.me();
      sf.configDefault(rm);
    }

    {
      SleepFactory sf = SleepFactory.me();
      IMesmerizer im = sf.getMesmerizer();

      String id = Test.randomString(10);

      JsonData a = new JsonData();
      a.change();
      a.id = id;
      im.sleep(a);
      JsonData b = (JsonData) im.wake(a.getClass(), id);
      eq(a, b, "json data");
      msg("json data: " + b);

      BinData c = new BinData();
      c.change();
      c.id = id;
      im.sleep(c);
      BinData d = (BinData) im.wake(c.getClass(), id);
      eq(c, d, "bin data");
      msg("bin data: " + d);
    }
  }


  static public abstract class TData implements ISleepwalker, Serializable {
    public int a = 1;
    public int b = 2;
    public long c = 3;
    public String d = "fdsa";
    public String id = null;

    public void change() {
      a = (int) Math.random() * 100;
      b = (int) Math.random() * 1000 + 100;
      c = (int) Math.random() * 10000 + 1000;
      d = Test.randomString(100);
    }

    public boolean equals(Object _o) {
      if (_o instanceof TData) {
        TData o = (TData) _o;
        return a == o.a && b == o.b && c == o.c
                && d.equals(o.d);
      }
      return false;
    }

    public String toString() {
      return "a=" + a + " b=" + b + " c=" + c + " d=" + d;
    }
  }


  static public class JsonData extends TData implements IJsonData, Serializable {
    @Override
    public String getid() {
      return id;
    }
  }


  static public class BinData extends TData implements IBinData, Serializable {
    @Override
    public String getid() {
      return id;
    }
  }


  public static void main(String[] a) throws Throwable {
    new TestSleep();
  }
}
