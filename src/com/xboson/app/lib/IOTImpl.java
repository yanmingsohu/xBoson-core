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
// 文件创建日期: 20-11-20 下午12:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/IOTImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.distributed.MultipleExportOneReference;
import com.xboson.iot.*;
import com.xboson.util.Ref;

import java.rmi.RemoteException;
import java.util.*;


public class IOTImpl extends RuntimeUnitImpl implements IAResource {


  public IOTImpl() {
    super(null);
  }


  @Override
  public String description() {
    return "app.module.iot.functions()";
  }


  public Object open() {
    PermissionSystem.applyWithApp(LicenseAuthorizationRating.class, this);
    return new RpcProxy();
  }


  public class RpcProxy implements IotConst {

    private final MultipleExportOneReference<IIoTRpc> mr;
    private final String paasUser;


    private RpcProxy() {
      mr = new MultipleExportOneReference<>(RPC_NAME);
      SysImpl sys = (SysImpl) ModuleHandleContext._get("sys");
      paasUser = sys.getUserIdByOpenId();

      ConfigHolder ch = ConfigHolder.me();
      if (ch.needInitConfig()) {
        ch.updateConfig();
      }
    }


    public void restore(String sid, String pid) throws RemoteException {
      mr.each(true, (i, node, remote) -> {
        remote.restore(paasUser, sid, pid);
        return true;
      });
    }


    public Object info(String scenesid, String productid)
            throws RemoteException
    {
      final List<WorkerInfo> list = new ArrayList<>();
      mr.each(false, (i, node, remote) -> {
        Collections.addAll(list, remote.info(paasUser, scenesid, productid));
        return true;
      });
      return list;
    }


    public void stopAll(String scenesid, String productid) throws RemoteException {
      mr.each(true, (i, node, remote) -> {
        remote.stopAll(paasUser, scenesid, productid);
        return true;
      });
    }


    public void stop(String sid, String pid, String node, String type, int index)
            throws RemoteException
    {
      IIoTRpc remote = mr.get(node);
      if (remote == null) {
        throw new RemoteException("Get node fail");
      }
      remote.stop(paasUser, sid, pid, node, type, index);
    }


    public void changed(String id) throws RemoteException {
      mr.each(true, (i, node, remote) -> {
        remote.changed(paasUser, id);
        return true;
      });
    }


    public boolean sendCommand(String devFullId, Map<String, Object> cmd)
            throws RemoteException
    {
      final Ref<Boolean> found = new Ref<>(false);
      mr.each(true, (i, node, remote) -> {
        found.x = remote.sendCommand(paasUser, devFullId, cmd);
        return !found.x;
      });
      return found.x;
    }


    public void encrypt(Map<String, Object> scriptDoc, String code)
            throws RemoteException
    {
      int z = (int) (Math.random() * Integer.MAX_VALUE);
      String scode = mr.random().encrypt(code, z);
      scriptDoc.put("z", z);
      scriptDoc.put("code", scode);
    }


    public void decrypt(Map<String, Object> scriptDoc) throws RemoteException {
      int z = (int) scriptDoc.get("z");
      String scode = (String) scriptDoc.get("code");
      String code = mr.random().decrypt(scode, z);
      scriptDoc.put("code", code);
    }


    public String dataId(String dev, String name, int dt, long d) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(d);
      return Util.dataId(dev, name, dt, c);
    }
  }
}
