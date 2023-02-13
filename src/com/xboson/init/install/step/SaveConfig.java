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
// 文件创建日期: 17-11-19 上午9:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/SaveConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.been.Config;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;


public class SaveConfig implements IStep {


  @Override
  public int order() {
    return 99;
  }


  @Override
  public boolean gotoNext(HttpData data) {
    String cjson = Tool.getAdapter(Config.class).toJson(data.cf);
    data.req.setAttribute("configstr", cjson);
    String act = data.req.getParameter("act");

    if ("reconfig".equals(act)) {
      data.reset = true;
    }
    else if ("restart".equals(act)) {
      try {
        String path = data.cf.configPath + com.xboson.init.Startup.INIT_FILE;
        File init_file = new File(path);
        FileWriter w = new FileWriter(init_file);
        w.write(new Date().toString());
        w.close();

        SysConfig.me().generateDefaultConfigFile(data.cf);
        data.msg = "系统即将重启...";
        return true;

      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    else {
      data.msg = "请选择一个操作";
    }

    return false;
  }


  @Override
  public String getPage(HttpData data) {
    return "save-config.jsp";
  }
}
