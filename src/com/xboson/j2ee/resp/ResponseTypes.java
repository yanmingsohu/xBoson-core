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
// 文件创建日期: 17-11-18 下午6:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/ResponseTypes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.resp;

import com.xboson.been.XBosonException;
import com.xboson.j2ee.container.IXResponse;

import java.util.HashMap;
import java.util.Map;


/**
 * 所有应答方式
 */
public final class ResponseTypes {

  private ResponseTypes() {}

  private static final Map<String, IXResponse> types = new HashMap<>();
  private static final String default_type = "json";

  /**
   * 支持列表
   */
  static {
    types.put("json",   new JsonResponse());
    types.put("xml",    new XmlResponse());
    types.put("jsonp",  new JsonPaddingResp());
    types.put("stream", new StreamResponse());
    types.put("null",   new NullResponse());
  }


  /**
   * 返回指定的应答方式, 无效的名称会抛出异常
   */
  public static IXResponse get(String name) {
    IXResponse xr = types.get(name);
    if (xr == null) {
      throw new XBosonException("response type not exist " + name);
    }
    return xr;
  }


  /**
   * 返回默认应答方式
   */
  public static IXResponse get() {
    return get(default_type);
  }
}
