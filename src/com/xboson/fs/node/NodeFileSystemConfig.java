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
// 文件创建日期: 17-12-22 下午12:26
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/node/NodeFileSystemConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.node;

import com.xboson.event.EventQueueMigrationThread;
import com.xboson.fs.redis.IFileSystemConfig;


public class NodeFileSystemConfig implements IFileSystemConfig {

  private EventQueueMigrationThread mt;
  private final String localPath;


  NodeFileSystemConfig(String local) {
    this.localPath = local;
  }


  @Override
  public String configQueueName() {
    return "XB.Node.File.ChangeQueue";
  }


  @Override
  public String configStructName() {
    return "XB.Node.File.Struct";
  }


  @Override
  public String configContentName() {
    return "XB.Node.File.CONTENT";
  }


  @Override
  public String configFileChangeEventName() {
    return "Node.file.change";
  }


  @Override
  public String configLocalPath() {
    return localPath;
  }


  @Override
  public void startMigrationThread() {
    if (mt == null) {
      mt = new EventQueueMigrationThread(this);
    }
  }
}
