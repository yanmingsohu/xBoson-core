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
// 文件创建日期: 20-11-23 下午5:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/IDeviceCommandProcessor.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import java.rmi.RemoteException;
import java.util.Map;


/**
 * 可以处理设备命令的数据处理器
 */
interface IDeviceCommandProcessor {


  /**
   * 向设备发送命令, 必须在 http 上下文中调用
   * @param inf 已经解析的主题
   * @param cmd 命令列表
   * @throws RemoteException 在执行时出现异常
   */
  void sendCommand(TopicInf inf, Map<String, Object> cmd) throws RemoteException;

}
