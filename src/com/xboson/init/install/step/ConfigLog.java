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
// 文件创建日期: 17-11-20 下午4:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/ConfigLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.log.Level;
import com.xboson.util.Tool;


public class ConfigLog  implements IStep {

  @Override
  public int order() {
    return 5;
  }


  @Override
  public boolean gotoNext(HttpData data) {
    data.cf.loggerWriterType =
            data.req.getParameter("log_type");
    data.cf.logLevel =
            data.req.getParameter("log_level");
    data.cf.logPath =
            data.cf.configPath + '/' + data.req.getParameter("log_path");

    try {
      if (Tool.isNulStr(data.cf.loggerWriterType)) {
        data.msg = "日志类型无效";
        return false;
      }

      if (Tool.isNulStr(data.cf.logLevel)) {
        data.msg = "日志级别无效";
        return false;
      }

      if (Tool.isNulStr(data.req.getParameter("log_path"))) {
        data.msg = "路径无效";
        return false;
      }

      Class.forName("com.xboson.log.writer." + data.cf.loggerWriterType);
      Level lv = Level.find(data.cf.logLevel);
      if (lv == Level.INHERIT) {
        throw new Error("全局禁止使用 INHERIT 级别");
      }
      data.cf.logLevel = lv.getName();

      return true;
    } catch(Exception e) {
      e.printStackTrace();
    }

    return false;
  }


  @Override
  public String getPage(HttpData data) {
    return "log.jsp";
  }
}
