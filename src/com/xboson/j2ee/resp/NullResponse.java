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
