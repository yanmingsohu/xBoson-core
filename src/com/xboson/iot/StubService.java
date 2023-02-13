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
// 文件创建日期: 20-11-23 上午7:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/StubService.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.xboson.event.EventLoop;
import com.xboson.event.OnExitHandle;
import org.bson.Document;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 本机实现
 */
public class StubService implements IIoTRpc {

  private final OnExitHandle systemExit;
  private Map<String, Procuct> prods;
  private Util util;


  StubService() {
    this.prods = new HashMap<>();
    this.util = new Util();
    this.systemExit = new Exit();
    beginAutoRestart();
  }


  private void beginAutoRestart() {
    EventLoop.me().add(()->{
      try {
        autoRestart();
        util.log.info("Auto restart all topic threads success");
      } catch(Exception e) {
        util.log.error("Cannot start topic thread", e);
        e.printStackTrace();
      }
    });
  }


  private void autoRestart() throws RemoteException {
    FindIterable<Document> it = util.openDb(TABLE_ADDRESS)
            .find(new Document("auto_restart", true));
    MongoCursor<Document> cursor = it.iterator();

    while (cursor.hasNext()) {
      Document doc = cursor.next();
      String fmt = doc.getString("fmt");
      String user = doc.getString("auto_auth");
      TopicInf inf = new TopicInf(fmt);

      util.log.debug("Preparation start", fmt);
      restore(user, inf.scenes, inf.product);
    }
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


  private void stopAllAndRelease() {
    for (Procuct p : prods.values()) {
      try {
        p.stopAll();
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    prods = null;
    util = null;
  }


  private class Exit extends OnExitHandle {

    @Override
    protected void exit() {
      stopAllAndRelease();
    }
  }
}
