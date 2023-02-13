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
// 文件创建日期: 18-1-30 上午10:11
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/ConfigCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.been.XBosonException;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.Tool;


public class ConfigCluster implements IStep {

  @Override
  public int order() {
    return 0;
  }


  @Override
  public boolean gotoNext(HttpData data) throws Exception {
    int nodeid = data.getInt("clusterNodeID");
    if (nodeid < 0 || nodeid > 1023) {
      throw new XBosonException("运算节点 有效范围 0~1023");
    }
    data.cf.clusterNodeID = (short) nodeid;

    String sp = data.getStr("sessionPassword");
    if (sp != null) {
      data.cf.sessionPassword = sp;
    } else {
      throw new XBosonException("会话密钥 不能为空");
    }

    int port = data.getInt("rpcPort");
    if (port > 0) {
      data.cf.rpcPort = port;
    }

    String ip = data.getStr("rpcIp");
    if (ip != null) {
      String[] ips = ip.split(",");
      Tool.pl(ips);
      for (int i=0; i<ips.length; ++i) {
        ips[i] = ips[i].trim();
        if (Tool.isNulStr(ips[i])) {
          throw new XBosonException("本机地址 格式错误");
        }
      }
      data.cf.rpcIp = ips;
    }

    data.cf.rpcUpnp = data.getBool("rpcUpnp");
    return true;
  }


  @Override
  public String getPage(HttpData data) {
    String sp = data.getStr("sessionPassword");
    if (sp == null) {
      sp = data.cf.sessionPassword;
    }
    data.req.setAttribute("sessionPassword", sp);
    return "cluster.jsp";
  }
}
