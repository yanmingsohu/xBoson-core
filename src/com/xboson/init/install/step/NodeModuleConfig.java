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
// 文件创建日期: 18-1-22 下午1:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/NodeModuleConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.Tool;


public class NodeModuleConfig implements IStep {

  @Override
  public int order() {
    return 7;
  }


  @Override
  public boolean gotoNext(HttpData data) throws Exception {
    String url = data.getStr("nodeUrl");
    String type = data.getStr("nodeProviderClass");
    data.cf.nodeUrl = url;
    data.cf.nodeProviderClass = type;

    if ("local".equals(type)) {
      if (Tool.isNulStr(url)) {
        data.msg = "本地模式必须设置 '静态文件根目录'";
        return false;
      }
      return data.isDirectory(url);

    } else if ("online".equals(type)) {
      return true;
    }

    data.msg = "无效的模式: " + type;
    return false;
  }


  @Override
  public String getPage(HttpData data) {
    return "nodemodule.jsp";
  }
}
