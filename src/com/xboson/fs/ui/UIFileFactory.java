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
// 文件创建日期: 17-12-17 下午6:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/UIFileFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.event.EventLoop;
import com.xboson.event.timer.EarlyMorning;
import com.xboson.fs.redis.*;
import com.xboson.util.SysConfig;


public final class UIFileFactory extends AbsFactory {

  private static UIFileFactory instance;


  public static UIFileFactory me() {
    if (instance == null) {
      synchronized (UIFileFactory.class) {
        if (instance == null) {
          instance = new UIFileFactory();
        }
      }
    }
    return instance;
  }


  /**
   * 使用配置文件中定义的参数创建全局唯一 ui 读取器.
   */
  public synchronized static IRedisFileSystemProvider open() {
    return me().__open();
  }


  protected IRedisFileSystemProvider createLocal(
          Config cf, IFileSystemConfig config) {
    RedisBase rb              = new RedisBase(config);
    RedisFileMapping rfm      = new UIRedisFileMapping(rb);
    UILocalFileMapping local  = new UILocalFileMapping(rfm, rb);

    //
    // 本地模式启动同步线程
    //
    SynchronizeFiles sf = new SynchronizeFiles(rb, rfm);
    EventLoop.me().add(sf);
    if (cf.enableUIFileSync) {
      EarlyMorning.add(sf);
    }
    return local;
  }


  protected IRedisFileSystemProvider createOnline(
          Config cf, IFileSystemConfig config) {
    RedisBase rb              = new RedisBase(config);
    RedisFileMapping rfm      = new UIRedisFileMapping(rb);
    return rfm;
  }


  protected IFileSystemConfig createConfig(Config cf) {
    return new UIFileSystemConfig(cf.uiUrl);
  }


  private UIFileFactory() {}


  @Override
  protected String providerType(Config cf) {
    return cf.uiProviderClass;
  }
}
