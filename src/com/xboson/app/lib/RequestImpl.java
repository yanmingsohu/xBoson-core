////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-23 下午12:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/RequestImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.been.CallData;
import jdk.nashorn.api.scripting.AbstractJSObject;

import java.util.Map;


/**
 * sys.request 的实现,
 * 对属性的读取映射到 http 参数上.
 *
 * 自定义参数优先级高于 http 参数.
 */
public class RequestImpl extends AbstractJSObject {

  private CallData cd;
  private Map<String, Object> extendParameter;


  public RequestImpl(CallData cd) {
    this.cd = cd;
    this.extendParameter = AppContext.me().getExtendParameter();
  }


  @Override
  public boolean hasMember(String name) {
    return extendParameter.containsKey(name)
            || cd.req.getParameter(name) != null;
  }


  @Override
  public Object getMember(String name) {
    Object ret = extendParameter.get(name);
    if (ret == null) {
      ret = cd.req.getParameter(name);
    }
    return ret;
  }
}
