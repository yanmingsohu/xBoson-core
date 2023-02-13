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
