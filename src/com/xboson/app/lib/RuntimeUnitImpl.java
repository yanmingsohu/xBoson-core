////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 上午11:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/RuntimeUnitImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * js 运行时抽象基类
 */
public abstract class RuntimeUnitImpl {

  /**
   * 请求数据包装对象, 子类直接使用
   */
  protected final CallData cd;


  public RuntimeUnitImpl(CallData cd) {
    this.cd = cd;
  }


  /**
   * 创建 js 环境中的 Array 对象
   * @param length 列表初始长度
   * @return
   */
  protected ScriptObjectMirror createJSList(int length) {
    NativeArray na = NativeArray.construct(true, null, length);
    ScriptObjectMirror list = (ScriptObjectMirror)
            ScriptObjectMirror.wrap(na, Context.getGlobal());
    return list;
  }


  /**
   * @see #createJSList(int)
   */
  protected ScriptObjectMirror createJSList() {
    return createJSList(0);
  }


  /**
   * js 传来的对象, 包装后才能在 java 中操作
   * @param obj 一个 ScriptObject 的子类
   * @return
   */
  protected ScriptObjectMirror wrap(Object obj) {
    Object ret = ScriptObjectMirror.wrap(obj, Context.getGlobal());
    if (ret instanceof ScriptObjectMirror) {
      return (ScriptObjectMirror) ret;
    } else {
      throw new XBosonException.BadParameter("object", "not js object");
    }
  }


  /**
   * @see #createJSObject(Map)
   */
  protected ScriptObjectMirror createJSObject() {
    return (ScriptObjectMirror) ScriptObjectMirror.wrap(
            Context.getGlobal().newObject(), Context.getGlobal());
  }


  /**
   * 使用 map 初始化创建的 js 对象的属性
   */
  protected ScriptObjectMirror createJSObject(
          Map<? extends String, ? extends Object> init) {
    ScriptObjectMirror js = createJSObject();
    js.putAll(init);
    return js;
  }


  /**
   * 包装 java byte 数组, 返回 js 数组类型对象.
   *
   * [实现细节] 使用 ArrayData.allocate(ByteBuffer.wrap(b)) 创建的数组对象,
   * 虽然不需要复制内存, 但是该数组对象无法再次通过实参调用 java 函数, 如果调用抛出
   * UnsupportedOperationException 异常.
   */
  protected Object wrapBytes(byte[] b) {
    ScriptObjectMirror js = createJSList(b.length);
    for (int i=0; i<b.length; ++i) {
      js.setSlot(i, b[i]);
    }
    return js;
  }


  /**
   * 创建对象数组的 key 集合
   * @param objs js 对象数组
   * @param attrName 每个对象使用该属性的值作为 set 的 key
   */
  protected Set<String> array2Set(Object[] objs, String attrName) {
    if (Tool.isNulStr(attrName))
      throw new XBosonException.NullParamException("String attrName");

    Set<String> ret = new HashSet<>();

    for (int i=0; i<objs.length; ++i) {
      ScriptObjectMirror cobj = wrap(objs[i]);
      if (cobj.hasMember(attrName)) {
        ret.add( String.valueOf(cobj.getMember(attrName)) );
      }
    }
    return ret;
  }


  /**
   * 针对 js 内部对象字符串化
   */
  protected String jsonStringify(Object o) {
    if (o != null) {
      o = ScriptUtils.unwrap(o);
      o = NativeJSON.stringify(this, o, null, null);
    }
    return String.valueOf(o);
  }


  /**
   * 解析 json 字符串转换为 js 内部对象
   */
  protected Object jsonParse(String str) {
    if (str == null)
      return null;
    return NativeJSON.parse(this, str, null);
  }
}
