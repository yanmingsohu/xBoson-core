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
// 文件创建日期: 17-11-20 下午5:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/UiConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.redis.LocalFileMapping;
import com.xboson.util.Tool;

import java.io.File;


public class UiConfig implements IStep {

  @Override
  public int order() {
    return 6;
  }


  @Override
  public boolean gotoNext(HttpData data) throws Exception {
    data.cf.uiListDir = Boolean.parseBoolean(
            data.req.getParameter("uiListDir"));

    data.cf.uiWelcome = data.req.getParameter("uiWelcome");
    if (Tool.isNulStr(data.cf.uiWelcome)) {
      data.msg = "必须设置根路径跳转";
      return false;
    }

    String clname = data.req.getParameter("uiProviderClass");
    try {
      if (!(clname.equals("local") || clname.equals("online"))) {
        data.msg = clname + " 不是 UI 文件映射接口";
        return false;
      }
    } catch (Exception e) {
      data.msg = "服务类型错误:" + e.getMessage();
      return false;
    }

    if (clname.equals("local")) {
      data.cf.uiUrl = data.req.getParameter("uiUrl");
      if (Tool.isNulStr(data.cf.uiUrl )) {
        data.msg = "请设置 '静态文件根目录'";
        return false;
      }
      return data.isDirectory(data.cf.uiUrl);

    } else {
      data.cf.uiUrl = "< Not use >";
    }
    return true;
  }


  @Override
  public String getPage(HttpData data) {
    return "ui.jsp";
  }
}
