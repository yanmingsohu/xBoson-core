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
// 文件创建日期: 18-5-22 上午9:34
// 原始文件路径: E:/xboson/xBoson/src/com/xboson/service/OlderApi.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;


@XPath("/api")
public class OlderApi extends XService {

  @Override
  public void service(CallData data) throws Exception {
    throw new UnsupportedOperationException(
            "Service '/api' Already obsolete; Use "+ App.PATH_FOTMAT);
  }


  @Override
  public boolean needLogin() {
    return false;
  }
}
