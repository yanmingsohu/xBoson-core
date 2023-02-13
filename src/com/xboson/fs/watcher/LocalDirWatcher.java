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
// 文件创建日期: 17-11-15 下午1:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/LocalDirWatcher.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.watcher;

import com.xboson.been.XBosonException;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 对本地文件系统目录的改变进行监控,
 * 仅对本层目录做监控, 深层目录需要另外启动监听.
 * 线程安全.
 */
public class LocalDirWatcher extends OnExitHandle implements Runnable {

  private static LocalDirWatcher instance;

  private final WatchService ws;
  private final Log log;
  private final Thread watchThread;
  private final Map<WatchKey, InnerData> map;
  private boolean running;


  private LocalDirWatcher() throws IOException {
    FileSystem fs = FileSystems.getDefault();
    ws = fs.newWatchService();
    log = LogFactory.create();
    map = new ConcurrentHashMap<>();
    running = true;

    watchThread = new Thread(this, "local-dir-watcher");
    watchThread.setDaemon(true);
    watchThread.start();

    log.info("Initialization Success");
  }


  public static LocalDirWatcher me() {
    if (instance == null) {
      synchronized (LocalDirWatcher.class) {
        if (instance == null) {
          try {
            instance = new LocalDirWatcher();
          } catch(Exception e) {
            throw new XBosonException(e);
          }
        }
      }
    }
    return instance;
  }


  /**
   * 监听目录中文件的变动
   */
  public IWatcher watchModify(Watchable w, INotify n) throws IOException {
    return _watch(w, n, StandardWatchEventKinds.ENTRY_MODIFY);
  }


  /**
   * 监听目录中文件的创建
   */
  public IWatcher watchCreate(Watchable w, INotify n) throws IOException {
    return _watch(w, n, StandardWatchEventKinds.ENTRY_CREATE);
  }


  /**
   * 监听目录中文件的删除
   */
  public IWatcher watchDelete(Watchable w, INotify n) throws IOException {
    return _watch(w, n, StandardWatchEventKinds.ENTRY_DELETE);
  }


  /**
   * 监听目录中的所有动作
   */
  public IWatcher watchAll(Watchable w, INotify n) throws IOException {
    return _watch(w, n,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);
  }


  private IWatcher _watch(Watchable w, INotify n, WatchEvent.Kind ...kind)
          throws IOException {
    InnerData d = new InnerData();
    d.n = n;
    d.w = w;
    WatchKey key = w.register(ws, kind);
    d.key = key;
    map.put(key, d);
    return d;
  }


  @Override
  public void run() {
    log.info("Local Watcher Thread is Running");
    WatchKey key;

    try {
      while (running) {
        key = ws.take();
        InnerData d = map.get(key);
        if (d == null) {
          // 多线程的时候 ConcurrentHashMap 反应迟钝
          //key.cancel();
          continue;
        }

        for (WatchEvent<?> event: key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          if (kind == StandardWatchEventKinds.OVERFLOW) {
            continue;
          }

          try {
            Object context = event.context();
            d.n.notify(d.w.toString(), context.toString(), event, kind);
          } catch(Exception e) {
            log.error(e, Tool.allStack(e));
          }
        }

        boolean valid = key.reset();
        if (!valid) {
          d.n.remove(d.w.toString());
          map.remove(key);
        }
      }
    } catch (InterruptedException x) {}

    log.info("Local Watcher Thread Stop");
  }


  @Override
  protected void exit() {
    running = false;
    if (watchThread.isAlive()) {
      watchThread.interrupt();
    }
    try {
      watchThread.join(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (WatchKey wk : map.keySet()) {
      wk.cancel();
    }
    map.clear();
    Tool.close(ws);
    instance = null;
  }


  private class InnerData implements IWatcher {
    private INotify n;
    private Watchable w;
    private WatchKey key;

    @Override
    public void close() throws Exception {
      map.remove(key);
      key.cancel();
    }
  }
}
