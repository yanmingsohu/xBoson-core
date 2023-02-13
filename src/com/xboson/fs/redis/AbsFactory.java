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
// 文件创建日期: 17-12-22 下午12:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/redis/AbsFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.util.SysConfig;


/**
 * 方便创建文件系统工厂
 */
public abstract class AbsFactory {

  private IRedisFileSystemProvider current;
  private IFileSystemConfig config;
  private final Config cf;


  public AbsFactory() {
    cf = SysConfig.me().readConfig();
  }


  /**
   * 使用配置文件中定义的参数创建全局唯一 ui 读取器.
   */
  protected synchronized IRedisFileSystemProvider __open() {
    if (current == null) {
      config    = getConfig();
      current   = create();
    }
    return current;
  }


  private IRedisFileSystemProvider create() {
    String type = providerType(cf);
    switch (type) {
      case "local":
        return createLocal(cf, config);

      case "online":
        return createOnline(cf, config);

      default:
        throw new XBosonException.NotImplements(
                "File system type: " + type);
    }
  }


  public IFileSystemConfig getConfig() {
    if (config == null) {
      config = createConfig(cf);
    }
    return config;
  }


  /**
   * 返回提供文件系统的底层类型
   */
  protected abstract String providerType(Config cf);


  /**
   * 创建本地文件系统
   */
  protected abstract IRedisFileSystemProvider createLocal(
          Config cf, IFileSystemConfig config);


  /**
   * 创建在线文件系统
   */
  protected abstract IRedisFileSystemProvider createOnline(
          Config cf, IFileSystemConfig config);


  /**
   * 创建配置文件, 该方法保证只调用一次
   */
  protected abstract IFileSystemConfig createConfig(Config cf);
}
