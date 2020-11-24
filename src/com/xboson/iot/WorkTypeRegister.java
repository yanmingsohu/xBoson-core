////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
