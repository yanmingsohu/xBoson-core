////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/StubService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 本机实现
 */
public class StubService implements IIoTRpc {

  private Map<String, Procuct> prods;
  private Util util;


  public StubService() {
    this.prods = new HashMap<>();
    this.util = new Util();
  }


  public synchronized void restore(String scenesid, String productid)
          throws RemoteException
  {
    Procuct p = getProcuct(scenesid, productid, false);
    if (p == null) {
      String pid = Util.id(scenesid, productid);
      util.hasProduct(scenesid, productid);
      p = new Procuct(pid, util);
      prods.put(pid, p);
    }

    p.restore();
  }


  @Override
  public synchronized WorkerInfo[] info(String sid, String pid)
          throws RemoteException
  {
    ArrayList<WorkerInfo> list = new ArrayList<>();
    Procuct p = getProcuct(sid, pid, false);
    if (p != null) {
      p.info(list);
    }
    return list.toArray(new WorkerInfo[0]);
  }


  @Override
  public synchronized void stopAll(String scenesid, String productid)
          throws RemoteException
  {
    Procuct p = getProcuct(scenesid, productid, true);
    p.stopAll();
  }


  @Override
  public synchronized void stop(String sid, String pid, String _node,
                                String type, int index)
                                throws RemoteException
  {
    int itype = WorkTypeRegister.get(type);
    Procuct p = getProcuct(sid, pid, true);
    IWorkThread w = p.find(itype, index);
    if (w == null) {
      throw new RemoteException("Could not find thread.");
    }
    if (! w.isRunning()) {
      throw new RemoteException("The Thread Not Running.");
    }

    w.stop();
  }


  private Procuct getProcuct(String sid, String pid, boolean checknull)
          throws RemoteException
  {
    util.checkAuth(sid);
    String id = Util.id(sid, pid);
    Procuct p = prods.get(id);
    if (checknull && p == null) {
      throw new RemoteException("Product not exists "+ id);
    }
    return p;
  }
}
