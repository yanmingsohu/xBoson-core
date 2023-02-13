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
// 文件创建日期: 18-1-3 下午2:42
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/mongo/SysMongoFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.xboson.been.MongoConfig;
import com.xboson.been.XBosonException;
import com.xboson.event.OnExitHandle;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;


/**
 * 创建全局唯一平台级 mongodb 连接
 */
public class SysMongoFactory extends OnExitHandle {

  public static final String DEFAULT_DISK = "disk";

  private static SysMongoFactory instance;

  private MongoConfig mc;
  private MongoClient cli;


  public static SysMongoFactory me() {
    if (instance == null) {
      synchronized (SysMongoFactory.class) {
        if (instance == null) {
          instance = new SysMongoFactory();
        }
      }
    }
    return instance;
  }


  private SysMongoFactory() {
    mc = SysConfig.me().readConfig().mongodb;
    if (!mc.enable)
      throw new XBosonException("MongoDB disabled");

    cli = new MongoClient(mc.address(), mc.credential(), mc.options());
  }


  @Override
  protected void exit() {
    synchronized (SysMongoFactory.class) {
      Tool.close(cli);
      cli = null;
      mc = null;
      instance = null;
    }
  }


  /**
   * 用默认磁盘名打开 mongodb 上的文件系统
   */
  public MongoFileSystem openFS() {
    return openFS(DEFAULT_DISK);
  }


  /**
   * 打开 mongodb 上的文件系统
   */
  public MongoFileSystem openFS(String diskName) {
    MongoDatabase db = cli.getDatabase(mc.database);
    return new MongoFileSystem(db, diskName);
  }
}
