////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-1-6 上午10:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/resp/NullResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.xboson.j2ee.container.IXResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


/**
 * 应答数据集被放置于 Header 中, 通常用于直接输出二进制流
 */
public class NullResponse implements IXResponse {

  @Override
  public void response(HttpServletRequest req, HttpServletResponse resp,
                       Map<String, Object> ret_root) throws IOException {
    for (String name : ret_root.keySet()) {
      resp.setHeader("X-"+ name, String.valueOf(ret_root.get(name)));
    }
  }
}
