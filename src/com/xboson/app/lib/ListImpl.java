////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ListImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.script.IJSObject;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.Context;

import java.util.Iterator;


public class ListImpl extends RuntimeUnitImpl implements IJSObject {

  public final static String RISE = "0";
  public Object array_sort_comparator;


  public ListImpl() {
    super(null);
  }


  @Override
  public String env_name() {
    return "list";
  }


  @Override
  public boolean freeze() {
    return false;
  }


  @Override
  public void init() {
  }


  @Override
  public void destory() {
  }


  public Object range(NativeArray arr, int begin, int end) {
    return NativeArray.slice(arr, begin, end);
  }


  public Object removeAt(NativeArray arr, int remove_index) {
    NativeArray.splice(arr, remove_index, 1);
    return arr;
  }


  public Object add(NativeArray arr, Object val) {
    NativeArray.push(arr, val);
    return arr;
  }


  public Object addAt(NativeArray arr, Object val, int index) {
    NativeArray.splice(arr, index, 0 , val);
    return arr;
  }


  public Object addAll(NativeArray arr, NativeArray src) {
    int end = src.size();
    for (int i=0; i<end; ++i) {
      NativeArray.push(arr, src.get(i));
    }
    return arr;
  }


  public Object reverse(NativeArray arr) {
    NativeArray.reverse(arr);
    return arr;
  }


  public String toString(NativeArray arr, Object sp) {
    return NativeArray.join(arr, sp);
  }


  public boolean contain(NativeArray arr, Object compareVal) {
    int end = arr.size();
    compareVal = ScriptObjectMirror.wrap(compareVal, Context.getGlobal());

    for (int i=0; i<end; ++i) {
      Object o = arr.get(i);
      if (_equals(compareVal, o)) {
        return true;
      }
    }
    return false;
  }


  /**
   * 深层比较两个 js 对象, 比较对象中的所有属性都相同返回 true.
   * 如果不是 js 对象, 进行简单比较.
   *
   * @param a 尽可能转换为 ScriptObjectMirror 的对象
   * @param b 与 a 比较
   * @return a==b 返回 true
   */
  private boolean _equals(Object a, Object b) {
    //
    // c 即是 b
    //
    Object c = ScriptObjectMirror.wrap(b, Context.getGlobal());
    //
    // c == b 说明没有转换成 ScriptObjectMirror
    // 执行简单比较
    //
    if (c == b) {
      return a.equals(b);
    }
    //
    // c 一定被转换了, 而 a 没有转换过, a b,一定不同
    //
    if (a instanceof ScriptObjectMirror == false) {
      return false;
    }

    ScriptObjectMirror x = (ScriptObjectMirror) c;
    ScriptObjectMirror y = (ScriptObjectMirror) a;

    if (x.equals(y))
      return true;

    if (x.size() != y.size())
      return false;

    Iterator<String> it = x.keySet().iterator();
    while (it.hasNext()) {
      String name = it.next();
      if (x.getMember(name).equals(y.getMember(name)) == false) {
        return false;
      }
    }
    return true;
  }


  public Object remove(NativeArray arr, Object removeVal) {
    int end = arr.size();
    removeVal = ScriptObjectMirror.wrap(removeVal, Context.getGlobal());

    for (int i=0; i<end; ++i) {
      Object o = arr.get(i);
      if (_equals(removeVal, o)) {
        NativeArray.splice(arr, i, 1);
        continue;
      }
    }
    return arr;
  }


  public Object sort(NativeArray arr, String... param) {
    NativeArray.sort(arr, array_sort_comparator);
    return arr;
  }

}
