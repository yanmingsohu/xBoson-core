////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIServerSocketFactory;


public class SafeServerFactory implements RMIServerSocketFactory, Serializable {

  public static final int SOCK_TIMEOUT = 7 * 1000;
  public static final int BUF_SIZE = 1024;

  private boolean updateRpcPort;
  private byte[] password;
  private double id;


  public SafeServerFactory(String password) {
    this(password, false);
  }


  public SafeServerFactory(String password, boolean updateRpcPort) {
    // Tool.pl("------------------", "Server Factory", id);
    this.password = AES.aesKey(password);
    this.id = Math.random();
    this.updateRpcPort = updateRpcPort;
  }


  @Override
  public ServerSocket createServerSocket(int port) throws IOException {
    // Tool.pl("------------------", "Create Server", port, id);
    ServerSocket server = new SaveServerSocket(port);
    if (updateRpcPort) {
      ClusterManager.me().updateRpcPort(server.getLocalPort());
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
