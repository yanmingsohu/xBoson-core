////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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


public interface IIoTRpc extends IXRemote, IotConst {


  /**
   * 恢复所有处理器线程
   * @param scenesid 场景
   * @param productid 产品
   * @throws RemoteException
   */
  void restore(String scenesid, String productid) throws RemoteException;


  /**
   * 返回所有线程状态
   */
  WorkerInfo[] info(String scenesid, String productid) throws RemoteException;


  /**
   * 停止所有线程
   */
  void stopAll(String scenesid, String productid) throws RemoteException;


  /**
   * 停止指定节点上的指定线程
   */
  void stop(String sid, String pid, String node, String type, int index)
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
   * @param id 脚本id
   * @throws RemoteException
   */
  void changed(String id) throws RemoteException;

}
