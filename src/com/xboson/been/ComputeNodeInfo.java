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
// 文件创建日期: 18-1-30 上午11:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ComputeNodeInfo.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.util.Network;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.util.Properties;


public class ComputeNodeInfo extends JsonHelper implements Cloneable {

  public long startAt;
  public String nodeID;
  public String[] ip;
  public String javaVersion;
  public String javaVendor;
  public String osName;
  public String osVersion;
  public String osArch;
  public int rpcPort;


  /**
   * 初始化空对象
   */
  public ComputeNodeInfo() {
  }


  /**
   * 初始化完整对象, ip 地址来自配置文件, 或未配置则来自本机地址列表.
   * 无效的 ip 将导致 rpc 无法连接或速度太慢.
   */
  public ComputeNodeInfo(String nodeid, int rpcPort) {
    Config cfg = SysConfig.me().readConfig();
    String[] ip = cfg.rpcIp;
    if (ip == null || ip.length < 1) {
      ip = Network.toAddressString(Network.netWorkerInterfaces());

      if (cfg.rpcIpMask != null && cfg.rpcIpMask.length() > 0) {
        ip = Network.filter(cfg.rpcIpMask, ip);
      }
    }

    Properties sysattr = System.getProperties();
    this.ip            = ip;
    this.javaVersion   = sysattr.getProperty("java.version");
    this.javaVendor    = sysattr.getProperty("java.vendor");
    this.osName        = sysattr.getProperty("os.name");
    this.osVersion     = sysattr.getProperty("os.version");
    this.osArch        = sysattr.getProperty("os.arch");
    this.nodeID        = nodeid;
    this.rpcPort       = rpcPort;
    this.startAt       = System.currentTimeMillis();
  }


  public ComputeNodeInfo clone() {
    ComputeNodeInfo n = new ComputeNodeInfo();
    n.startAt     = startAt;
    n.nodeID      = nodeID;
    n.ip          = Tool.copy(ip);
    n.javaVersion = javaVersion;
    n.javaVendor  = javaVendor;
    n.osName      = osName;
    n.osVersion   = osVersion;
    n.osArch      = osArch;
    n.rpcPort     = rpcPort;
    return n;
  }
}
