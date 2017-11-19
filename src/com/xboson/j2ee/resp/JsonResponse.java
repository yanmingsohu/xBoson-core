////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午5:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/JsonResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.squareup.moshi.JsonAdapter;
import com.xboson.been.ResponseRoot;
import com.xboson.j2ee.container.IXResponse;
import com.xboson.util.OutputStreamSinkWarp;
import com.xboson.util.Tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;


public class JsonResponse implements IXResponse {

  private static final String MIME_JSON = "application/json; charset=utf-8";

  private final JsonAdapter<ResponseRoot> jadapter;


  public JsonResponse() {
    jadapter = Tool.getAdapter(ResponseRoot.class);
  }


  @Override
  public void response(HttpServletRequest request, HttpServletResponse response,
                       ResponseRoot ret_root) throws IOException {

    OutputStream out = response.getOutputStream();
    OutputStreamSinkWarp outwarp = new OutputStreamSinkWarp(out);

    response.setHeader("content-type", MIME_JSON);
    jadapter.toJson(outwarp, ret_root);
  }

}
