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
// 文件创建日期: 17-11-19 上午9:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/RestartServer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.init.SystemFlag;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;


public class RestartServer implements IStep {


  @Override
  public int order() {
    return 100;
  }


  @Override
  public boolean gotoNext(HttpData data) {
    return false;
  }


  @Override
  public String getPage(HttpData data) {
    GlobalEventBus.me().emit(Names.exit, RestartServer.class);
    if (! SystemFlag.canRestart) {
      data.msg = "您需要手动重启 Servlet 容器..";
    }
    return "restarting.jsp";
  }

}
