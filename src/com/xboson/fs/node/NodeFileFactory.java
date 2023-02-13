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
// 文件创建日期: 17-12-22 下午12:25
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/node/NodeFileFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.node;

import com.xboson.been.Config;
import com.xboson.event.EventLoop;
import com.xboson.event.timer.EarlyMorning;
import com.xboson.fs.redis.*;
import com.xboson.script.IConfigurableModuleProvider;
import com.xboson.script.IModuleProvider;


public final class NodeFileFactory extends AbsFactory {

  private static NodeFileFactory instance;


  /**
   * 打开 node 文件系统
   */
  public static IRedisFileSystemProvider open() {
    return me().__open();
  }


  private synchronized static NodeFileFactory me() {
    if (instance == null) {
      instance = new NodeFileFactory();
    }
    return instance;
  }


  /**
   * 使用当前 node 文件系统创建模块读取器
   * @param parent 只当父级模块返回 null 时才从文件系统中创建.
   * @return
   */
  public synchronized static IConfigurableModuleProvider
        openNodeModuleProvider(IModuleProvider parent)
  {
    IRedisFileSystemProvider fs = open();
    IFileSystemConfig config = me().getConfig();
    NodeModuleProvider node = new NodeModuleProvider(fs, config, parent);
    return node;
  }


  @Override
  protected IRedisFileSystemProvider createLocal(
          Config cf, IFileSystemConfig config) {
    RedisBase rb              = new RedisBase(config);
    RedisFileMapping rfm      = new NodeRedisFileMapping(rb);
    LocalFileMapping local    = new NodeLocalFileMapping(rfm, rb);

    SynchronizeFiles sf = new SynchronizeFiles(rb, rfm);
    EventLoop.me().add(sf);
    if (cf.enableNodeFileSync) {
      EarlyMorning.add(sf);
    }
    return local;
  }


  @Override
  protected IRedisFileSystemProvider createOnline(
          Config cf, IFileSystemConfig config) {
    RedisBase rb              = new RedisBase(config);
    RedisFileMapping rfm      = new NodeRedisFileMapping(rb);
    return rfm;
  }


  @Override
  protected IFileSystemConfig createConfig(Config cf) {
    return new NodeFileSystemConfig(cf.nodeUrl);
  }


  private NodeFileFactory() {}


  @Override
  protected String providerType(Config cf) {
    return cf.nodeProviderClass;
  }
}
