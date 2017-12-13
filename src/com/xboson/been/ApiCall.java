////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-4 上午8:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ApiCall.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.util.Map;


/**
 * 封装对 api 的调用
 */
public class ApiCall implements IBean {

  /** 必须, HTTP 请求参数 */
  public CallData call;

  /** 必须, 机构 id */
  public String org;

  /** 必须, 应用 id */
  public String app;

  /** 必须, 模块 id */
  public String mod;

  /** 必须, 接口 id */
  public String api;

  /** 扩展请求参数, 优先级高于 http 参数, 可以 null */
  public Map<String, Object> exparam;

}
