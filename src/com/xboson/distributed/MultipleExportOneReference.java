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
// 文件创建日期: 18-12-9 下午12:03
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/distributed/MultipleExportOneReference.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.distributed;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.rpc.ClusterManager;
import com.xboson.rpc.IXRemote;
import com.xboson.rpc.RpcFactory;
import com.xboson.util.Tool;

import java.rmi.RemoteException;
import java.util.Set;


/**
 * 集群服务模式, 所有节点将服务导出到集群中, 使用时遍历所有节点上的服务
 */
public class MultipleExportOneReference<T extends IXRemote> {

  private ClusterManager cm;
  private RpcFactory rpc;
  private String name;
  private Log log;


  public interface For<E extends IXRemote> {

    /**
     * 每个节点上的服务调用该方法
     * @param index 数量索引
     * @param nodeid 节点索引
     * @param service 节点上的服务
     * @return 如果返回 false, 则不再迭代更多节点
     */
    boolean node(int index, String nodeid, E service) throws RemoteException;
  }


  /**
   * 指定服务名
   */
  public MultipleExportOneReference(String name) {
    this.rpc  = RpcFactory.me();
    this.cm   = ClusterManager.me();
    this.name = name;
    this.log  = LogFactory.create("MEOR:"+ name);
  }


  /**
   * 只绑定服务一次
   */
  public boolean bindOnce(IXRemote remote) {
    return rpc.bindOnce(remote, name);
  }


  /**
   * 遍历所有服务, 异常节点被忽略
   */
  public void each(For<T> getter) throws RemoteException {
    each(false, getter);
  }


  /**
   * 遍历所有服务, 如果遇到错误且 throwErr == true 则抛出异常, 否则忽略
   */
  public void each(boolean throwErr, For<T> getter) throws RemoteException {
    int i = 0;

    for (String node : cm.list()) {
      try {
        T service = (T) rpc.lookup(node, name);
        if (! getter.node(i, node, service)) {
          break;
        }
        ++i;
      } catch(Exception e) {
        if (throwErr) throw e;
        log.error("Connect node", node, "fail,", Tool.allStack(e));
      }
    }
  }


  /**
   * 随机返回一个节点上的服务
   */
  public T random() throws RemoteException {
    for (String node : cm.list()) {
      return (T) rpc.lookup(node, name);
    }
    throw new RemoteException("Cannot find any node");
  }


  public T get(String node) {
    return (T) rpc.lookup(node, name);
  }


  public RpcFactory getRpc() {
    return rpc;
  }
}
