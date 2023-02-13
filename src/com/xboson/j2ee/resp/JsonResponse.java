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
// 文件创建日期: 17-11-18 下午5:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/JsonResponse.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.squareup.moshi.JsonAdapter;
import com.xboson.j2ee.container.IXResponse;
import com.xboson.util.OutputStreamSinkWarp;
import com.xboson.util.Tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


public class JsonResponse implements IXResponse {

  private static final String MIME_JSON = "application/json; charset=utf-8";

  private final JsonAdapter<Map> jadapter;


  public JsonResponse() {
    jadapter = Tool.getAdapter(Map.class);
  }


  @Override
  public void response(HttpServletRequest request, HttpServletResponse response,
                       Map<String, Object> ret_root) throws IOException {

    OutputStream out = response.getOutputStream();
    OutputStreamSinkWarp outwarp = new OutputStreamSinkWarp(out);

    response.setHeader("content-type", MIME_JSON);
    jadapter.toJson(outwarp, ret_root);
  }

}
