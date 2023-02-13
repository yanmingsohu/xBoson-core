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
// 文件创建日期: 19-1-20 下午8:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/rpc/RpcGlobalInitList.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.rpc;

import com.xboson.app.lib.PmImpl;
import com.xboson.distributed.XLock;
import com.xboson.iot.WorkTypeRegister;


/**
 * 所有 RPC 对象需要在启动时注册到注册表中, 把注册代码写到这里.
 */
public final class RpcGlobalInitList {


  /**
   * RPC 注册列表, 包外禁止访问
   */
  static final void init(RpcFactory rpc) {
    PmImpl.regTo(rpc);
    WorkTypeRegister.regTo(rpc);
    XLock.me();
  }

}
