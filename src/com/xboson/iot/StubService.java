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

import org.bson.Document;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 本机实现
 *TODO 自动重启线程
 */
public class StubService implements IIoTRpc {

  private Map<String, Procuct> prods;
  private Util util;


  StubService() {
    this.prods = new HashMap<>();
    this.util = new Util();
  }


  @Override
  public synchronized void restore(String paasUser, String scenesid, String productid)
          throws RemoteException
  {
    Procuct p = getProcuct(paasUser, scenesid, productid, false);
    if (p == null) {
      String pid = Util.id(scenesid, productid);
      util.hasProduct(paasUser, scenesid, productid);
      p = new Procuct(pid, util);
      prods.put(pid, p);
    }

    p.restore(paasUser);
  }


  @Override
  public synchronized WorkerInfo[] info(String paasUser, String sid, String pid)
          throws RemoteException
  {
    ArrayList<WorkerInfo> list = new ArrayList<>();
    Procuct p = getProcuct(paasUser, sid, pid, false);
    if (p != null) {
      p.info(list);
    }
    return list.toArray(new WorkerInfo[0]);
  }


  @Override
  public synchronized void stopAll(String paasUser, String scenesid, String productid)
          throws RemoteException
  {
    Procuct p = getProcuct(paasUser, scenesid, productid, true);
    p.stopAll();
  }


  @Override
  public synchronized void stop(String paasUser, String sid, String pid,
                                String _node, String type, int index)
                                throws RemoteException
  {
    int workType = WorkTypeRegister.get(type);
    Procuct p = getProcuct(paasUser, sid, pid, true);
    IWorkThread w = p.find(workType, index);
    if (w == null) {
      throw new RemoteException("Could not find thread.");
    }
    if (! w.isRunning()) {
      throw new RemoteException("The Thread Not Running.");
    }

    w.stop();
  }


  /**
   * 返回产品线程集对象
   * @param sid 场景, 做权限检查
   * @param pid 产品
   * @param checkNull 如果产品不存在是否抛出异常
   * @return
   * @throws RemoteException
   */
  private Procuct getProcuct(String paasUser, String sid, String pid, boolean checkNull)
          throws RemoteException
  {
    util.checkAuth(paasUser, sid);
    String id = Util.id(sid, pid);
    Procuct p = prods.get(id);
    if (checkNull && p == null) {
      throw new RemoteException("Product not exists "+ id);
    }
    return p;
  }


  @Override
  public String encrypt(String code, int z) throws RemoteException {
    return Util.secr.encryptApi2(code, z);
  }


  @Override
  public String decrypt(String dcode, int z) throws RemoteException {
    return new String(Util.secr.decryptApi2(dcode, z));
  }


  @Override
  public void changed(String paasUser, String id) throws RemoteException {
    List<Document> or = new ArrayList<>();
    or.add(new Document("owner", paasUser));
    or.add(new Document("share", paasUser));

    Document filter = new Document("_id", id);
    filter.put("$or", or);

    if (util.openDb("script").count(filter) < 1) {
      throw new RemoteException("No auth changed script");
    }
    util.changed(id);
  }


  @Override
  public synchronized boolean sendCommand(String paasUser, String devFullId,
                                          Map<String, Object> cmd)
          throws RemoteException
  {
    TopicInf inf = TopicInf.parseID(devFullId);
    Procuct p = getProcuct(paasUser, inf.scenes, inf.product, false);
    if (p != null) {
      IDeviceCommandProcessor cp = p.findCmdProcessor();
      if (cp != null) {
        cp.sendCommand(inf, cmd);
        return true;
      }
    }
    return false;
  }
}
