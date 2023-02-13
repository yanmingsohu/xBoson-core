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
// 文件创建日期: 20-11-23 上午7:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/WorkTypeRegister.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.rpc.RpcFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;


public class WorkTypeRegister {

  private static final Map<String, Integer> typeInt;
  private static final Class<IWorkThread>[] work_type = new Class[] {
          null, // index 0 is null
          DataTopicProcess.class,
          EventTopicProcess.class,
          StateTopicProcess.class,
          null, // cmd not use
          SaveTopicProcess.class,
  };


  static {
    typeInt = initTypeMap();
  }


  private static Map<String, Integer> initTypeMap() {
    Map<String, Integer> map = new HashMap<>();
    for (int i=1; i<work_type.length; ++i) {
      try {
        Class<IWorkThread> cl = work_type[i];
        if (cl != null) {
          String name = cl.newInstance().name();
          map.put(name, i);
        }
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    return map;
  }


  /**
   * 通过类型名返回类型代码
   * @param name 类型名
   * @throws RemoteException 找不到抛出异常
   */
  static int get(String name) throws RemoteException {
    Integer type = typeInt.get(name);
    if (type == null) {
      throw new RemoteException("Invaild type "+ name);
    }
    return type;
  }


  /**
   * 返回类型的实现类
   * @param type 从1开始类型代码
   * @throws RemoteException 找不到抛出异常
   */
  static Class<IWorkThread> get(int type) throws RemoteException {
    if ((type < 1) || (type >= work_type.length) || (work_type[type] == null)) {
      throw new RemoteException("Invalid type "+ type);
    }
    return work_type[type];
  }


  public static void regTo(RpcFactory rpc) {
    rpc.bindOnce(new StubService(), IotConst.RPC_NAME);
  }
}
