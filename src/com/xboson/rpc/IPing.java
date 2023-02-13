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
// 文件创建日期: 18-1-30 下午4:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/IPing.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import java.rmi.RemoteException;


/**
 * 如果远程对象实现了 IPing 接口, 在从缓存取出前会检查网络连通情况.
 */
public interface IPing extends IXRemote {

  String PONG = "PONG";


  default String ping(String a) throws RemoteException {
    return a;
  }


  default String ping() throws RemoteException {
    return PONG;
  }
}
