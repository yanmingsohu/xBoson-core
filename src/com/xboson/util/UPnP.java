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
// 文件创建日期: 19-1-21 下午4:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/UPnP.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.event.EventLoop;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;

import java.net.InetAddress;
import java.util.Map;


/**
 * 所有的方法都是异步的, 方法立即返回结果在日志中记录.
 */
public class UPnP {

  public static final String TCP = "TCP";
  public static final String UDP = "UDP";
  /** 映射主机的地址由网关检测 */
  public static final String LOCAL_HOST = null;
  /** 固定描述 */
  public static final String desc = "xBoson-system";

  private static UPnP instance;

  private GatewayDiscover discover;
  private Log log;


  private UPnP() {
    log = LogFactory.create("upnp");
    discover = new GatewayDiscover();
  }


  public static UPnP me() {
    if (instance == null) {
      synchronized (UPnP.class) {
        if (instance == null) {
          instance = new UPnP();
        }
      }
    }
    return instance;
  }


  /**
   * 在网关上添加 TCP 端口映射
   *
   * @param local 内网端口
   * @param gateway 外网端口
   * @param ip 内网地址
   */
  public void setTcpPortMapping(int local, int gateway, String ip) {
    setPortMapping(local, gateway, ip, TCP);
  }


  /**
   * 在网关上添加 TCP 端口映射
   *
   * @param port 内网/外网端口
   * @param ip 内网地址
   */
  public void setTcpPortMapping(int port, String ip) {
    setTcpPortMapping(port, port, ip);
  }


  /**
   * 在网关上添加 UDP 端口映射
   *
   * @param local 内网端口
   * @param gateway 外网端口
   * @param ip 内网地址
   */
  public void setUdpPortMapping(int local, int gateway, String ip) {
    setPortMapping(local, gateway, ip, UDP);
  }


  /**
   * 在网关上添加 UDP 端口映射
   *
   * @param port 内网/外网端口
   * @param ip 内网地址
   */
  public void setUdpPortMapping(int port, String ip) {
    setUdpPortMapping(port, port, ip);
  }


  private void setPortMapping(int l, int o, String ip, String protocol) {
    findGateway();

    EventLoop.me().add(()->{
      Map<InetAddress, GatewayDevice> gateways = discover.getAllGateways();
      if (gateways.isEmpty()) {
        return;
      }

      String _ip = ip;

      for (Map.Entry<InetAddress, GatewayDevice> it: gateways.entrySet()) {
        try {
          GatewayDevice gw = it.getValue();
          if (_ip == LOCAL_HOST) {
            _ip = gw.getLocalAddress().getHostAddress();
          }
          if (gw.isConnected()) {
            if (gw.addPortMapping(o, l, _ip, protocol, desc)) {
              log.info("Port Mapping", _ip+':'+l, "TO", o, "success");
              continue;
            }
          }
          gateways.remove(it.getKey());
          log.error("Port Mapping", _ip+':'+l, "TO", o, "failed");
        } catch (Exception e) {
          log.error("Gateway fail", e);
        }
      }
    });
  }


  /**
   * 删除外网 TCP 端口映射
   * @param port 外网端口
   */
  public void rmTcpPortMapping(int port) {
    rmPortMapping(port, TCP);
  }


  /**
   * 删除外网 UDP 端口映射
   * @param port 外网端口
   */
  public void rmUdpPortMapping(int port) {
    rmPortMapping(port, UDP);
  }


  private void rmPortMapping(int port, String protocol) {
    findGateway();

    EventLoop.me().add(()->{
      Map<InetAddress, GatewayDevice> gateways = discover.getAllGateways();
      if (gateways.isEmpty()) {
        return;
      }

      for (Map.Entry<InetAddress, GatewayDevice> it: gateways.entrySet()) {
        try {
          GatewayDevice gw = it.getValue();
          if (gw.isConnected()) {
            if (gw.deletePortMapping(port, protocol)) {
              log.info("Remove Port Mapping", protocol, port, "success");
              continue;
            }
          }
          gateways.remove(it.getKey());
          log.info("Remove Port Mapping", protocol, port, "failed");
        } catch (Exception e) {
          log.error("Gateway fail", e);
        }
      }
    });
  }


  private void findGateway() {
    if (! discover.getAllGateways().isEmpty()) {
      return;
    }

    EventLoop.me().add(()->{
      try {
        log.info("Looking for Gateway Devices...");
        Map<InetAddress, GatewayDevice> gateways = discover.discover();
        if (gateways.isEmpty()) {
          log.error("No gateways found");
        }
      } catch (Exception e) {
        log.error("Find gateway", e);
      }
    });
  }
}
