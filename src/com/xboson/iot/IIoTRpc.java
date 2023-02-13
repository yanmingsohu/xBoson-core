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
// 文件创建日期: 20-11-23 上午7:09
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/IIoTRpc.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.rpc.IXRemote;

import java.rmi.RemoteException;
import java.util.Map;


public interface IIoTRpc extends IXRemote, IotConst {


  /**
   * 恢复所有处理器线程
   * @param paasUser 平台用户
   * @param scenesid 场景
   * @param productid 产品
   * @throws RemoteException
   */
  void restore(String paasUser, String scenesid, String productid)
          throws RemoteException;


  /**
   * 返回所有线程状态
   */
  WorkerInfo[] info(String paasUser, String scenesid, String productid)
          throws RemoteException;


  /**
   * 停止所有线程
   */
  void stopAll(String paasUser, String scenesid, String productid)
          throws RemoteException;


  /**
   * 停止指定节点上的指定线程
   */
  void stop(String paasUser, String sid, String pid, String node, String type, int index)
          throws RemoteException;


  /**
   * 加密代码
   * @param code 代码
   * @param z 密钥索引
   * @return 加密的代码
   */
  String encrypt(String code, int z) throws RemoteException;


  /**
   * 解密代码
   * @param dcode 已经加密的代码
   * @param z 密码索引
   * @return 代码原文
   */
  String decrypt(String dcode, int z) throws RemoteException;


  /**
   * 通知线程, 脚本更改
   * @param paasUser 平台用户
   * @param id 脚本id
   * @throws RemoteException
   */
  void changed(String paasUser, String id) throws RemoteException;


  /**
   * 向设备发送命令, 必须在 http 上下文中调用
   * @param paasUser 平台用户
   * @param devFullId 设备完整 id
   * @param cmd 命令列表
   * @throws RemoteException 执行时出现异常, 如果节点没有找到可执行命令的线程不会抛出异常
   * @return 如果命令被成功发送返回 true, 否则返回 false
   */
  boolean sendCommand(String paasUser, String devFullId, Map<String, Object> cmd)
          throws RemoteException;

}
