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
// 文件创建日期: 18-12-9 下午1:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/distributed/MasterMode.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.distributed;

import com.xboson.been.Config;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.c0nst.IConstant;


/**
 * 集群服务模式, 服务由主节点提供, 其他节点总是引用主节点上的服务
 */
public class MasterMode<T extends IXRemote> implements IConstant {

  private RpcFactory rpc;
  private boolean isMaster;
  private T local_service;
  private String rpcName;


  /**
   * 该类的实例通常为静态属性并在系统初始化时创建, 初始化顺序很重要,
   * 因为其他节点可以在任何时候访问主节点.
   *
   * @param rpcName 服务名称
   * @param local 服务类构造器, 在非 Master 节点上, 不会实例化本地服务.
   */
  public MasterMode(String rpcName, ILocalServiceCreator<T> local) {
    this.rpcName = rpcName;
    this.rpc = RpcFactory.me();
    Config cf = SysConfig.me().readConfig();
    this.isMaster = cf.clusterNodeID == MASTER_NODE;

    if (isMaster) {
      this.local_service = local.newInstance();
      rpc.bindOnce(local_service, rpcName);
    } else {
      this.local_service = null;
    }
  }


  /**
   * 返回集群上的服务
   */
  public T get() {
    if (isMaster) {
      return local_service;
    } else {
      return (T) rpc.lookup(MASTER_NODE_STR, rpcName);
    }
  }
}
