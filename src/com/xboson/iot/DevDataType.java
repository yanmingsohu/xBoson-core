////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 下午4:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/DevDataType.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;


import com.xboson.been.XBosonException;


public class DevDataType {

  interface ITransform {
    int DDT_int     = 1;
    int DDT_float   = 2;
    int DDT_virtual = 3;
    int DDT_sw      = 4;
    int DDT_string  = 5;

    Object t(Object i);
  }


  public static ITransform getTransform(String type) {
    switch (Integer.parseInt(type)) {
      case ITransform.DDT_int:
        return new Tint();

      case ITransform.DDT_float:
        return new Tfloat();

      case ITransform.DDT_string:
        return new Tstring();

      case ITransform.DDT_sw:
        return new Tsw();

      case ITransform.DDT_virtual:
        return new Tvirtual();

    }

    throw new XBosonException("Invaild type "+ type);
  }


  private static void nn(Object o) {
    if (o == null)
      throw new XBosonException("Data cannot be null");
  }


  private static class Tint implements ITransform {

    @Override
    public Object t(Object i) {
      nn(i);
      if (i instanceof Integer) {
        return i;
      }
      if (i instanceof Long) {
        return i;
      }
      if (i instanceof Float) {
        return ((Float) i).intValue();
      }
      if (i instanceof Double) {
        return ((Double) i).longValue();
      }
      if (i instanceof Boolean) {
        return ((boolean)i) ? 1 : 0;
      }
      if (i instanceof String) {
        return Long.parseLong((String) i);
      }
      throw new XBosonException("Cannot convert to Integer "+ i);
    }
  }


  private static class Tfloat implements ITransform {

    @Override
    public Object t(Object i) {
      nn(i);
      if (i instanceof Integer) {
        return ((Integer) i).floatValue();
      }
      if (i instanceof Long) {
        return ((Long) i).doubleValue();
      }
      if (i instanceof Float) {
        return i;
      }
      if (i instanceof Double) {
        return i;
      }
      if (i instanceof Boolean) {
        return ((boolean)i) ? 1.0 : 0.0;
      }
      if (i instanceof String) {
        return Double.parseDouble((String) i);
      }
      throw new XBosonException("Cannot convert to Float "+ i);
    }
  }


  private static class Tsw implements ITransform {

    @Override
    public Object t(Object i) {
      nn(i);
      if (i instanceof Integer) {
        return (int)i == 0;
      }
      if (i instanceof Long) {
        return (long)i == 0;
      }
      if (i instanceof Float) {
        return (float)i == 0;
      }
      if (i instanceof Double) {
        return (double)i == 0;
      }
      if (i instanceof Boolean) {
        return ((Boolean)i);
      }
      if (i instanceof String) {
        return Double.parseDouble((String) i);
      }
      throw new XBosonException("Cannot convert to switch "+ i);
    }
  }


  private static class Tvirtual implements ITransform {

    @Override
    public Object t(Object i) {
      nn(i);
      return i;
    }
  }


  private static class Tstring implements ITransform {

    @Override
    public Object t(Object i) {
      nn(i);
      return i.toString();
    }
  }
}
