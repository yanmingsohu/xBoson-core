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
// 文件创建日期: 18-1-30 上午11:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/ClusterManager.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.been.ComputeNodeInfo;
import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.io.IOException;
import java.util.Set;


/**
 * 集群管理器
 */
public final class ClusterManager extends OnExitHandle {

  public static final String HNAME = "XB.Cluster.ComputeNodes";

  private static ClusterManager instance;
  private final Log log;
  private final String nodeID;
  private final Short nodeid;
  private ComputeNodeInfo info;


  private ClusterManager() {
    Config cf   = SysConfig.me().readConfig();
    this.log    = LogFactory.create();
    this.nodeid = cf.clusterNodeID;
    this.nodeID = Short.toString(cf.clusterNodeID);
    this.info   = new ComputeNodeInfo(nodeID, cf.rpcPort);
    registerSelf();
    NodeUpdate.emit(nodeID);
  }


  @Override
  protected void exit() {
    try (IRedis client = RedisMesmerizer.me().open()) {
      client.hdel(HNAME, nodeID);
    }
  }


  public void updateRpcPort(int rpcPort) {
    if (info.rpcPort == rpcPort) return;
    info.rpcPort = rpcPort;
    registerSelf();
  }


  private void registerSelf() {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String str = info.toJSON();
      client.hset(HNAME, nodeID, str);
      log.debug("Cluster Node registered:", str);
    }
  }


  public String localNodeID() {
    return nodeID;
  }


  public short localNodeIDs() {
    return nodeid;
  }


  /**
   * 返回集群中所有节点 id.
   */
  public Set<String> list() {
    try (IRedis client = RedisMesmerizer.me().open()) {
      return client.hkeys(HNAME);
    }
  }


  /**
   * 获取集群中节点的信息, 节点不存在返回 null.
   */
  public ComputeNodeInfo info(String id) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String str = client.hget(HNAME, id);
      if (str == null) return null;
      return Tool.getAdapter(ComputeNodeInfo.class).fromJson(str);
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  /**
   * 返回本机节点描述对象, 返回的对象是克隆体可以任意处理.
   */
  public ComputeNodeInfo localInfo() {
    return info.clone();
  }


  public static ClusterManager me() {
    if (instance == null) {
      synchronized (ClusterManager.class) {
        if (instance == null) {
          instance = new ClusterManager();
        }
      }
    }
    return instance;
  }
}
