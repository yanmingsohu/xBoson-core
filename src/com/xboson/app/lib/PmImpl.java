////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-27 上午9:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/PmImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.app.IProcessState;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.been.PublicProcessData;
import com.xboson.been.XBosonException;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/**
 * 进程管理器, 支持集群
 */
public class PmImpl implements IAResource {

  public static final String RPC_NAME = "XB.rpc.ProcessManager";


  public PmImpl() {
    try {
      RpcFactory rpc = RpcFactory.me();
      if (! rpc.isBind(RPC_NAME)) {
        rpc.bind(new ExportRemote(), RPC_NAME);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


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
    return "app.module.apipm.functions()";
  }


  public interface IPM extends IXRemote {
    PublicProcessData[] list() throws RemoteException;
    int kill(long processId) throws RemoteException;
    int stop(long processId) throws RemoteException;
  }


  /**
   * 本机实现
   */
  public static class Local implements IProcessState {

    public final int KILL_OK        = IProcessState.KILL_OK;
    public final int KILL_NO_EXIST  = IProcessState.KILL_NO_EXIST;
    public final int KILL_NO_READY  = IProcessState.KILL_NO_READY;
    public final int KILL_IS_KILLED = IProcessState.KILL_IS_KILLED;

    private RpcFactory fact = RpcFactory.me();
    private ClusterManager cm = ClusterManager.me();


    public PublicProcessData[] list() throws RemoteException {
      List<PublicProcessData> list = new ArrayList<>();
      for (String node : cm.list()) {
        try {
          IPM pm = (IPM) fact.lookup(node, RPC_NAME);
          for (PublicProcessData pd : pm.list()) {
            list.add(pd);
          }
        } catch (Exception e) {}
      }
      return list.toArray(new PublicProcessData[list.size()]);
    }


    public int kill(String nodeID, long processId) throws RemoteException {
      IPM pm = (IPM) fact.lookup(nodeID, RPC_NAME);
      return pm.kill(processId);
    }


    public int stop(String nodeID, long processId) throws RemoteException {
      return kill(nodeID, processId);
    }
  }


  /**
   * 导出到集群中
   */
  private static class ExportRemote implements IPM {
    private AppContext.ProcessManager pm;


    private ExportRemote() {
      pm = AppContext.me().getProcessManager();
    }


    @Override
    public PublicProcessData[] list() {
      return pm.list();
    }


    @Override
    public int kill(long processId) {
      return pm.kill(processId);
    }


    @Override
    public int stop(long processId) {
      return pm.stop(processId);
    }
  }

}
