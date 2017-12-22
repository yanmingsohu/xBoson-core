////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.redis.RedisBase;
import com.xboson.fs.redis.RedisFileMapping;
import com.xboson.fs.redis.SynchronizeFiles;
import com.xboson.util.SysConfig;


public final class UIFileFactory {

  private static UIFileSystemConfig config;
  private static IRedisFileSystemProvider current;


  /**
   * 使用配置文件中定义的参数创建全局唯一 ui 读取器.
   */
  public static IRedisFileSystemProvider openWithConfig() {
    if (current == null) {
      synchronized (UIFileFactory.class) {
        if (current == null) {
          Config cf = SysConfig.me().readConfig();
          config    = new UIFileSystemConfig(cf.uiUrl);
          current   = create(cf);
        }
      }
    }
    return current;
  }


  private static IRedisFileSystemProvider create(Config cf) {
    switch (cf.uiProviderClass) {
      case "local":
        return createLocal(cf);

      case "online":
        return createOnline(cf);

      default:
        throw new XBosonException.NotImplements(
                "UI File system type: " + cf.uiProviderClass);
    }
  }


  private static IRedisFileSystemProvider createLocal(Config cf) {
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


  private static IRedisFileSystemProvider createOnline(Config cf) {
    RedisBase rb              = new RedisBase(config);
    RedisFileMapping rfm      = new UIRedisFileMapping(rb);
    return rfm;
  }


  private UIFileFactory() {}
}
