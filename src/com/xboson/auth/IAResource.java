////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-15 下午3:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/IAResource.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * 被权限主体请求的资源, 必须能唯一准确的描述一个资源.
 */
public interface IAResource {

  /**
   * 将资源使用一个 URI 来描述, 两个不同的资源返回的 URI 也必须不同, 否则必须相同.
   *
   * @see File#toURI() 该方法名称的来源
   */
  URI toURI() throws URISyntaxException;

}
