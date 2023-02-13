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
// 文件创建日期: 17-12-22 上午9:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/UIFileSystemConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.event.EventQueueMigrationThread;
import com.xboson.fs.redis.IFileSystemConfig;


/**
 * 该对象维持了唯一一个文件消息队列
 */
public class UIFileSystemConfig implements IFileSystemConfig {

  private EventQueueMigrationThread mt;
  private final String localPath;


  public UIFileSystemConfig(String localPath) {
    this.localPath = localPath;
  }


  @Override
  public String configQueueName() {
    return "XB.UI.File.ChangeQueue";
  }


  @Override
  public String configStructName() {
    return "XB.UI.File.Struct";
  }


  @Override
  public String configContentName() {
    return "XB.UI.File.CONTENT";
  }


  public String configFileChangeEventName() {
    return "ui.file.change";
  }


  @Override
  public String configLocalPath() {
    return localPath;
  }


  @Override
  public synchronized void startMigrationThread() {
    if (mt == null) {
      mt = new EventQueueMigrationThread(this);
    }
  }
}
