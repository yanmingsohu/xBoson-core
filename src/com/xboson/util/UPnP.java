////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
