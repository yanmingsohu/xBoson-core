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
// 文件创建日期: 20-11-23 上午7:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/IWorkThread.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.app.lib.IOTImpl;

import java.rmi.RemoteException;


public interface IWorkThread extends IotConst {

  /**
   * 启动线程
   * @param pid    产品id
   * @param qos    数据质量
   * @param user   mq用户信息
   * @param script 脚本, 可以空
   * @param index  线程索引, 从0开始, 作为 WorkerInfo.tid
   * @throws RemoteException
   */
  void start(Util util, String pid, int qos, DeviceUser user, String script, int index)
          throws RemoteException;


  /**
   * 停止线程并等待, 如果线程已经停止则立即返回
   * @throws RemoteException 如果停止失败
   */
  void stop() throws RemoteException;


  /**
   * 如果线程正在运行返回 true
   * 保证已经停止的线程可以安全回收内存
   */
  boolean isRunning();


  /**
   * 返回线程状态
   */
  WorkerInfo info();


  /**
   * 返回 topic 名称
   */
  String name();
}
