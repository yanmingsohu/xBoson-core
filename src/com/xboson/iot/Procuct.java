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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/Procuct.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.rpc.ClusterManager;
import org.bson.Document;

import java.rmi.RemoteException;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;


public class Procuct implements IotConst {

  private Map<Integer, Deque<IWorkThread>> workers;
  private Util util;
  private String id;


  Procuct(String id, Util util) {
    this.workers = new HashMap<>();
    this.util = util;
    this.id = id;
  }


  void restore(String paasUser) throws RemoteException {
    clearDead();
    Document addr = util.openAddress(id);
    restore(paasUser, TYPE_DATA,  (Document) addr.get("data"));
    restore(paasUser, TYPE_EVENT, (Document) addr.get("event"));
    restore(paasUser, TYPE_STATE, (Document) addr.get("state"));
    restore(paasUser, TYPE_SAVE,  (Document) addr.get("save"));
  }


  void stopAll() throws RemoteException {
    stop(TYPE_DATA, 0);
    stop(TYPE_EVENT, 0);
    stop(TYPE_STATE, 0);
    stop(TYPE_SAVE, 0);
  }


  private void restore(String paasUser, int type, Document topicConf) throws RemoteException {
    int count = 0;
    if (topicConf != null) {
      count = targetCount( topicConf.getLong("count") );
    }

    if (count(type) < count) {
      String deviceUser = topicConf.getString("user");
      String script = topicConf.getString("script");
      Number qos = (Number) topicConf.get("qos");
      DeviceUser user = util.getUser(paasUser, deviceUser);

      start(type, count, qos.intValue(), user, script);
    } else {
      stop(type, count);
    }
  }


  /**
   * 当总任务为 c 的时候, 计算当前节点分配任务量
   */
  private int targetCount(Long c) {
    if (c == null) return 0;

    ClusterManager cm = ClusterManager.me();
    String self = cm.localNodeID();
    Object[] nodes = cm.list().toArray();
    if (nodes.length == 1) return c.intValue();

    int myindex = 0;
    for (int i=0; i<nodes.length; ++i) {
      if (self.equals(nodes[i])) {
        myindex = i;
        break;
      }
    }

    final int nodeCount = nodes.length;
    int[] cluster = new int[nodeCount];
    int node = 0;

    while (c > 0) {
      cluster[node % nodeCount]++;
      --c;
      ++node;
    }
    return cluster[myindex];
  }


  /**
   * 启动 type 类型的任务直到 count 个
   */
  private void start(int type, int count, int qos, DeviceUser user, String script)
          throws RemoteException
  {
    for (int i=0; i<count; ++i) {
      try {
        IWorkThread worker = find(type, i);
        if (worker != null && worker.isRunning()) {
          continue;
        } else {
          worker = newWorker(type);
        }

        worker.start(util, id, qos, user, script, i);
        pushWork(type, worker);
      } catch (Exception e) {
        throw new RemoteException("Start work "+ i +" fail: "+ e.getMessage());
      }
    }
  }


  /**
   * 终止任务, 直到线程数量达到 count
   */
  private void stop(int type, int count) throws RemoteException {
    Deque<IWorkThread> stack = workers.get(type);
    if (stack == null) return;

    while (stack.size() > count) {
      IWorkThread work = stack.getLast();
      work.stop();
      stack.removeLast();
    }
  }


  /**
   * 寻找指定线程,
   * @param type 类型
   * @param index 索引
   * @return 找不到返回 null
   */
  IWorkThread find(int type, int index) {
    Deque<IWorkThread> stack = workers.get(type);
    if (stack == null) return null;

    for (IWorkThread work : stack) {
      if (work.info().tid == index) {
        return work;
      }
    }
    return null;
  }


  IDeviceCommandProcessor findCmdProcessor() throws RemoteException {
    for (int type : workers.keySet()) {
      Class<IWorkThread> tc = WorkTypeRegister.get(type);

      if (IDeviceCommandProcessor.class.isAssignableFrom(tc)) {
        Deque<IWorkThread> stack = workers.get(type);
        if (stack == null) break;

        for (IWorkThread work : stack) {
          if (work.isRunning()) {
            return (IDeviceCommandProcessor) work;
          }
        }
      }
    }
    return null;
  }


  /**
   * 返回所有线程的状态
   */
  void info(List<WorkerInfo> list) {
    for (int type : workers.keySet()) {
      Deque<IWorkThread> stack = workers.get(type);
      for (IWorkThread w : stack) {
        list.add(w.info());
      }
    }
  }


  private IWorkThread newWorker(int type) throws RemoteException {
    try {
      Class<IWorkThread> tc = WorkTypeRegister.get(type);
      return tc.newInstance();
    } catch (Exception e) {
      throw new RemoteException("Cannot create new work "+ e.getMessage());
    }
  }


  private void pushWork(int type, IWorkThread worker) {
    Deque<IWorkThread> stack = workers.get(type);
    if (stack == null) {
      stack = new LinkedBlockingDeque<>(MAX_THREAD);
      workers.put(type, stack);
    }
    stack.addLast(worker);
  }


  private int count(int type) {
    if (! workers.containsKey(type)) {
      return 0;
    }
    return workers.get(type).size();
  }


  /**
   * 清除死线程
   */
  private void clearDead() {
    for (int type : workers.keySet()) {
      Deque<IWorkThread> stack = workers.get(type);

      for (int i= stack.size()-1; i>=0; --i) {
        IWorkThread w = stack.pollFirst();
        if (w != null && w.isRunning()) {
          stack.addLast(w);
        }
      }
    }
  }
}
