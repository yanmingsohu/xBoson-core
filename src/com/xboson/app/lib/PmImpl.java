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
import com.xboson.distributed.MultipleExportOneReference;
import com.xboson.distributed.ProcessManager;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.Tool;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/**
 * 进程管理器, 支持集群.
 * 算法: 将本地任务导出到集群, 操作时遍历所有节点.
 */
public class PmImpl implements IAResource {

  public static final String RPC_NAME = "XB.rpc.ProcessManager";


  public PmImpl() {
  }


  public static void regTo(RpcFactory rpc) {
    rpc.bindOnce(new ExportRemote(), RPC_NAME);
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
  public class Local implements IProcessState {

    public final int KILL_OK        = IProcessState.KILL_OK;
    public final int KILL_NO_EXIST  = IProcessState.KILL_NO_EXIST;
    public final int KILL_NO_READY  = IProcessState.KILL_NO_READY;
    public final int KILL_IS_KILLED = IProcessState.KILL_IS_KILLED;

    private MultipleExportOneReference<IPM> meof;


    private Local() {
      meof = new MultipleExportOneReference<>(RPC_NAME);
    }


    /**
     * 收集所有节点的数据并返回
     */
    public PublicProcessData[] list() throws RemoteException {
      List<PublicProcessData> list = new ArrayList<>();
      meof.each((i, node, pm) -> {
        for (PublicProcessData pd : pm.list()) {
          list.add(pd);
        }
        return true;
      });
      return list.toArray(new PublicProcessData[list.size()]);
    }


    public int kill(String nodeID, long processId) throws RemoteException {
      IPM pm = (IPM) meof.getRpc().lookup(nodeID, RPC_NAME);
      return pm.kill(processId);
    }


    public int stop(String nodeID, long processId) throws RemoteException {
      return kill(nodeID, processId);
    }
  }


  /**
   * 所有节点导出到集群中
   */
  private static class ExportRemote implements IPM {
    private ProcessManager pm;


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
