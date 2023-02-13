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
// 文件创建日期: 18-1-30 下午1:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/IXRemote.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * 使用类名注册到注册表中;
 * 方法签名中必须抛出 RemoteException 异常.
 *
 * @see RemoteException
 */
public interface IXRemote extends Remote, Serializable {

  /**
   * 返回 rpc 名称用于在 rpc 注册表中注册自身实例, 仅在未提供定义名称时被调用
   * @return 默认返回类名
   */
  default String getRpcName() throws RemoteException {
    return getClass().getName();
  }

}
