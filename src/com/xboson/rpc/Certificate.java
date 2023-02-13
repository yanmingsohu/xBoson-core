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
// 文件创建日期: 18-1-30 下午3:20
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/Certificate.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.auth.PermissionException;
import com.xboson.util.AES;
import com.xboson.util.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;


public class Certificate {

  private InputStream i;
  private OutputStream o;
  private Socket sock;
  private byte[] ps;


  Certificate(Socket sock, byte[] ps) throws IOException {
    this.sock = sock;
    this.ps   = ps;
    this.o    = sock.getOutputStream();
    this.i    = sock.getInputStream();
  }


  /**
   * 发送一个认证数据包
   *
   * @param size 数据包长度
   * @return
   * @throws IOException
   * @throws PermissionException 如果认真失败抛出异常
   */
  public void send(int size) throws IOException, PermissionException {
    byte[] wbuf = Tool.randomBytes(size);
    o.write(wbuf);

    wbuf = AES.Encode(wbuf, ps);
    byte[] rbuf = new byte[wbuf.length];

    if (i.read(rbuf) != rbuf.length)
      throw new PermissionException("bad certificate");

    if (! Arrays.equals(wbuf, rbuf))
      throw new PermissionException("bad certificate");
  }


  /**
   * 接收一个认证包
   *
   * @param size 数据包长度
   * @return
   * @throws IOException
   * @throws PermissionException
   */
  public void recv(int size) throws IOException, PermissionException {
    byte[] rbuf = new byte[size];

    if (i.read(rbuf) != size)
      throw new PermissionException("bad certificate");

    byte[] wbuf = AES.Encode(rbuf, ps);
    o.write(wbuf);
  }


  /**
   * 该方法用来关闭底层 socket, 认证失败后调用
   */
  public void close() {
    Tool.close(sock);
    sock = null;
  }
}
