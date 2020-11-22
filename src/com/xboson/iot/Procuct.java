////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-11-23 上午7:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/Procuct.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.app.lib.IOTImpl;
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


  public Procuct(String id, Util util) {
    this.workers = new HashMap<>();
    this.util = util;
    this.id = id;
  }


  void restore() throws RemoteException {
    clearDead();
    Document addr = util.openAddress(id);
    restore(TYPE_DATA,  (Document) addr.get("data"));
    restore(TYPE_EVENT, (Document) addr.get("event"));
    restore(TYPE_STATE, (Document) addr.get("state"));
    restore(TYPE_SAVE,  (Document) addr.get("save"));
  }


  void stopAll() throws RemoteException {
    stop(TYPE_DATA, 0);
    stop(TYPE_EVENT, 0);
    stop(TYPE_STATE, 0);
    stop(TYPE_SAVE, 0);
  }


  private void restore(int type, Document topicConf) throws RemoteException {
    int count = 0;
    if (topicConf != null) {
      count = targetCount( topicConf.getLong("count") );
    }

    if (count(type) < count) {
      String user = topicConf.getString("user");
      String script = topicConf.getString("script");
      long qos = topicConf.getLong("qos");

      start(type, count, (int)qos, user, script);
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
  private void start(int type, int count, int qos, String user, String script)
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
