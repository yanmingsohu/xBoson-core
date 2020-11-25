////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.iot.IIoTRpc;
import com.xboson.iot.IotConst;
import com.xboson.iot.Util;
import com.xboson.iot.WorkerInfo;
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
    }


    public void restore(String sid, String pid) throws RemoteException {
      mr.each((i, node, remote) -> {
        remote.restore(paasUser, sid, pid);
        return true;
      });
    }


    public WorkerInfo[] info(String scenesid, String productid)
            throws RemoteException
    {
      final List<WorkerInfo> list = new ArrayList<>();
      mr.each((i, node, remote) -> {
        Collections.addAll(list, remote.info(paasUser, scenesid, productid));
        return true;
      });
      return list.toArray(new WorkerInfo[0]);
    }


    public void stopAll(String scenesid, String productid) throws RemoteException {
      mr.each((i, node, remote) -> {
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
      mr.each((i, node, remote) -> {
        remote.changed(paasUser, id);
        return true;
      });
    }


    public boolean sendCommand(String devFullId, Map<String, Object> cmd)
            throws RemoteException
    {
      final Ref<Boolean> found = new Ref<>(false);
      mr.each((i, node, remote) -> {
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
