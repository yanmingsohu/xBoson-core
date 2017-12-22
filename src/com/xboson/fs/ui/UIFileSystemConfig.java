////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
  public void startMigrationThread() {
    if (mt == null) {
      mt = new EventQueueMigrationThread(this);
    }
  }
}