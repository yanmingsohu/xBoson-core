////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-30 下午1:38
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/SafeClientFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.been.ComputeNodeInfo;
import com.xboson.util.AES;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.rmi.server.RMIClientSocketFactory;


public class SafeClientFactory implements RMIClientSocketFactory, Serializable {

  public static final int BUF_SIZE = SafeServerFactory.BUF_SIZE;
  public static final int SOCK_TIMEOUT = SafeServerFactory.SOCK_TIMEOUT;

  private byte[] password;
  private ComputeNodeInfo node;
  private double id;


  public SafeClientFactory(String password) {
    this(password, null);
  }


  /**
   * 如果 node 非空, 则创建连接时使用 node 中提供的 ip 地址进行尝试.
   * node 参数是为了将本地对象传输到远端时为创建 tpc 连接提供信息.
   */
  public SafeClientFactory(String password, ComputeNodeInfo node) {
    // Tool.pl("------------------", "Client Factory", id);
    this.password = AES.aesKey(password);
    this.node = node;
    this.id = Math.random();
  }


  /**
   * 连接时不使用参数提供的 host 而是用节点提供的连接
   */
  @Override
  public Socket createSocket(String host, int port) throws IOException {
    if (node == null) {
      return connect(host, port);
    }

    String[] ip = node.ip;

    for (int i=0; i<ip.length; ++i) {
      try {
        return connect(ip[i], port);
      } catch (SocketTimeoutException e) {
        /* 如果超时则尝试另一个 ip 地址 */
      }
    }

    throw new SocketException("Cannot connect to node: "+ node.toJSON());
  }


  private Socket connect(String host, int port) throws IOException {
    // Tool.pl("------------------", "Create Socket", host, port, node, id);
    InetSocketAddress to = new InetSocketAddress(host, port);
    Socket sock = new Socket();
    sock.connect(to, SOCK_TIMEOUT);
    sock.setSoTimeout(SOCK_TIMEOUT);
    Certificate c = new Certificate(sock, password);

    try {
      c.recv(BUF_SIZE);
      c.send(BUF_SIZE);
      return sock;
    } catch (IOException e) {
      c.close();
      throw e;
    }
  }


  public String toString() {
    return "RpcClientFactory { ID: "+ id +" \nNODE:"+ node +"}";
  }
}
