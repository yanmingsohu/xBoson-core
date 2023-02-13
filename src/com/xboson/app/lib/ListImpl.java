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
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.Context;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ListImpl extends RuntimeUnitImpl implements IJSObject {

  public final static String RISE = "0";
  public Object array_sort_implement_js;


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


  public Object range(ScriptObjectMirror arr, int begin, int end) {
    return arr.callMember("slice", begin, end);
  }


  public Object removeAt(ScriptObjectMirror arr, int remove_index) {
    arr.callMember("splice", remove_index, 1);
    return arr;
  }


  public Object add(ScriptObjectMirror arr, Object val) {
    try {
      arr.callMember("push", val);
      return arr;
    } catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }


  public Object addAt(ScriptObjectMirror arr, Object val, int index) {
    arr.callMember("splice", index, 0, val);
    return arr;
  }


  public Object addAll(ScriptObjectMirror arr, Object src) {
    ScriptObjectMirror jsarr = wrap(src);
    int end = jsarr.size();
    for (int i=0; i<end; ++i) {
      arr.callMember("push", jsarr.getSlot(i));
    }
    return arr;
  }


  public Object reverse(ScriptObjectMirror arr) {
    arr.callMember("reverse");
    return arr;
  }


  public String toString(ScriptObjectMirror arr, Object sp) {
    return (String) arr.callMember("join", sp);
  }


  public boolean contain(Object oarr, Object compareVal) {
    ScriptObjectMirror arr = wrap(oarr);
    compareVal = ScriptObjectMirror.wrap(compareVal, Context.getGlobal());
    final int end = arr.size();

    for (int i=0; i<end; ++i) {
      Object o = arr.getSlot(i);
      if (_equals(compareVal, o)) {
        return true;
      }
    }
    return false;
  }


  /**
   * 深层比较两个 js 对象, 比较对象中的所有属性都相同返回 true.
   * 如果不是 js 对象, 进行简单比较.
   * 脚本环境中原始对象还是原始对象(int,float,string),
   * 其他复杂对象被包装到 ScriptObjectMirror 中.
   *
   * @param a 尽可能转换为 ScriptObjectMirror 的对象
   * @param b 与 a 比较
   * @return a==b 返回 true
   */
  private boolean _equals(Object a, Object b) {
    //
    // b 不是复杂对象, 执行简单比较
    //
    if (b instanceof ScriptObjectMirror == false) {
      return a.equals(b);
    }
    //
    // b 一定是复杂对象, 而 a 不是, a b,一定不同
    //
    if (a instanceof ScriptObjectMirror == false) {
      return false;
    }

    ScriptObjectMirror x = (ScriptObjectMirror) b;
    ScriptObjectMirror y = (ScriptObjectMirror) a;

    if (x.equals(y))
      return true;

    if (x.size() != y.size())
      return false;

    Set<String> names = new HashSet<>(x.size() << 1);
    names.addAll(x.keySet());
    names.addAll(y.keySet());

    for (String name : names) {
      Object o1 = x.getMember(name);
      Object o2 = y.getMember(name);

      if (o1 == null) {
        if (o2 == null) {
          continue;
        } else {
          return false;
        }
      }

      if (! o1.equals(o2)) {
        return false;
      }
    }
    return true;
  }


  public Object remove(ScriptObjectMirror jsarr, Object removeVal) {
    removeVal = ScriptObjectMirror.wrap(removeVal, Context.getGlobal());

    for (int i=0; i<jsarr.size(); ++i) {
      Object o = jsarr.getSlot(i);
      if (_equals(removeVal, o)) {
        jsarr.callMember("splice", i, 1);
        continue;
      }
    }
    return jsarr;
  }


  public Object sort(Object arr, String... param) {
    if (array_sort_implement_js == null)
      throw new XBosonException.NotExist("sort function not init");

    ScriptObjectMirror sort = wrap(array_sort_implement_js);
    if (! sort.isFunction() )
      throw new XBosonException.NotExist("sort function fail.");

    sort.call(unwrap(arr), param);
    return arr;
  }

}
