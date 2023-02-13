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
// 文件创建日期: 2017年11月3日 上午10:38:36
// 原始文件路径: xBoson/src/com/xboson/service/App.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.service;

import com.xboson.app.AppContext;
import com.xboson.been.ApiCall;
import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;

import javax.servlet.ServletException;
import java.io.IOException;


@XPath("/app")
public class App extends XService {

  public static final String PATH_FOTMAT
          = "Path format: /app/{org-id}/{app-id}/{module-id}/{api-name}";


  public App() {
  }


  @Override
  public void service(CallData data) throws ServletException, IOException {
    OpenApp.banAnonymous(data);

    AppContext af = AppContext.me();
    data.url.setErrorMessage(PATH_FOTMAT);

    ApiCall ac = new ApiCall(data.url);
    ac.call = data;

    af.call(ac);
  }

}
