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
// 文件创建日期: 18-1-31 下午2:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/Cluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.ComputeNodeInfo;
import com.xboson.been.XBosonException;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IPing;
import com.xboson.rpc.RpcFactory;

import java.rmi.RemoteException;
import java.util.Set;


public class Cluster implements IAResource {


  public Object open() {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);

    boolean runOnSysOrg = (boolean)
            ModuleHandleContext._get("runOnSysOrg");

    if (!runOnSysOrg)
      throw new XBosonException.NotImplements("只能在平台机构中引用");

    return new Local();
  }


  @Override
  public String description() {
    return "app.module.cluster.functions()";
  }


  public class Local {
    private ClusterManager cm;

    public final int RUNNING = 1;
    public final int DOWN    = -1;
    public final int UNKNOW  = 0;


    private Local() {
      cm = ClusterManager.me();
    }


    public String[] list() {
      Set<String> set = cm.list();
      return set.toArray(new String[set.size()]);
    }


    public ComputeNodeInfo info(String id) {
      return cm.info(id);
    }


    public int state(String id) {
      int s;
      try {
        IPing p = (IPing) RpcFactory.me().lookup(id, RpcFactory.PING);
        p.ping();
        s = RUNNING;
      } catch (RemoteException e) {
        e.printStackTrace();
        s = UNKNOW;
      } catch (Exception e) {
        e.printStackTrace();
        s = DOWN;
      }
      return s;
    }
  }
}
