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
// 文件创建日期: 17-11-15 下午2:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/watcher/INotify.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.watcher;

import java.io.IOException;
import java.nio.file.WatchEvent;


/**
 * 当文件有变动时, 通知该对象
 */
public interface INotify {

  /**
   * 通知文件的变动
   * @param basename 正在监听的目录
   * @param filename 改变的文件
   * @param event 变动消息对象
   * @param kind 变动类型
   * @throws IOException
   */
  void notify(String basename, String filename,
              WatchEvent event, WatchEvent.Kind kind) throws IOException;


  /**
   * 监听的目录被删除时调用
   * @param basename 正在监听的目录
   */
  void remove(String basename);
}
