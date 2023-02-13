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
// 文件创建日期: 18-2-7 下午2:35
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/watcher/SingleFile.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.watcher;

import com.xboson.util.Tool;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;


/**
 * 监听单个文件的包装器
 */
public class SingleFile implements INotify, Closeable {

  /** 接收事件的最小间隔, 防止多次触发 */
  public static final long MIN_INTERVAL = 1000;

  private String fileName;
  private INotify recv;
  private long last;
  private IWatcher watcher;

  /**
   * 构造单文件监听器, 该方法成功返回后, 文件处于监听状态.
   *
   * @param basePath 文件所在目录
   * @param fileName 文件名
   * @param recv 当文件被修改, 该接口接收修改事件
   */
  public SingleFile(String basePath, String fileName, INotify recv)
          throws IOException {
    this.fileName = fileName;
    this.recv = recv;

    Path path = Paths.get(basePath);
    watcher = LocalDirWatcher.me().watchModify(path, this);
  }


  @Override
  public void notify(String basename,
                     String filename,
                     WatchEvent event,
                     WatchEvent.Kind kind)
          throws IOException
  {
    long now = System.currentTimeMillis();
    if (now - last < MIN_INTERVAL)
      return;

    last = now;

    if (filename.equals(this.fileName)) {
      recv.notify(basename, filename, event, kind);
    }
  }


  @Override
  public void remove(String basename) {
    recv.remove(basename);
  }


  @Override
  public void close() throws IOException {
    Tool.close(watcher);
  }
}
