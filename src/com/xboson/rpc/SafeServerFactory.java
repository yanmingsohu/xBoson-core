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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/SafeServerFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.util.AES;
import com.xboson.util.UPnP;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIServerSocketFactory;


public class SafeServerFactory implements RMIServerSocketFactory, Serializable {

  public static final int SOCK_TIMEOUT = 7 * 1000;
  public static final int BUF_SIZE = 1024;

  private boolean useUpnp;
  private boolean updateRpcPort;
  private byte[] password;
  private double id;


  public SafeServerFactory(String password) {
    this(password, false, false);
  }


  public SafeServerFactory(String password, boolean updateRpcPort, boolean upnp) {
    // Tool.pl("------------------", "Server Factory", id);
    this.password = AES.aesKey(password);
    this.id = Math.random();
    this.updateRpcPort = updateRpcPort;
    this.useUpnp = upnp;
  }


  @Override
  public ServerSocket createServerSocket(int port) throws IOException {
    // Tool.pl("------------------", "Create Server", port, id);
    ServerSocket server = new SaveServerSocket(port);
    if (updateRpcPort) {
      ClusterManager.me().updateRpcPort(server.getLocalPort());
    }
    if (useUpnp) {
      UPnP.me().setTcpPortMapping(port, UPnP.LOCAL_HOST);
    }
    return server;
  }


  public String toString() {
    return "RpcServerFactory { ID: "+ id +"}";
  }


  public void setUpdateRpcPort(boolean set) {
    this.updateRpcPort = set;
  }


  private class SaveServerSocket extends ServerSocket {

    public SaveServerSocket(int port) throws IOException {
      super(port);
    }


    @Override
    public Socket accept() throws IOException {
      Socket sock = super.accept();
      sock.setSoTimeout(SOCK_TIMEOUT);

      Certificate c = new Certificate(sock, password);
      try {
        c.send(BUF_SIZE);
        c.recv(BUF_SIZE);
      } catch (Exception e) {
        c.close();
        throw e;
      }
      return sock;
    }
  }
}
