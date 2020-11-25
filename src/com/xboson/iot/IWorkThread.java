////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
