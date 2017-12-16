////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-20 上午10:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/FileModifyHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.been.XBosonException;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;

import javax.naming.event.NamingEvent;


/**
 * 封装 UI 文件修改通知到接口
 */
public class FileModifyHandle extends GLHandle {

  private IFileModify fm;


  public FileModifyHandle(IFileModify fm) {
    if (fm == null)
      throw new XBosonException.NullParamException("IFileModify fm");

    this.fm = fm;
    GlobalEventBus.me().on(Names.ui_file_change, this);
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    String mark_file = (String) namingEvent.getNewBinding().getObject();
    String file = mark_file.substring(1);
    char mark = mark_file.charAt(0);

    switch(mark) {
      case RedisBase.PREFIX_DIR:
        fm.makeDir(file);
        return;

      case RedisBase.PREFIX_FILE:
        fm.modify(file);
        return;

      case RedisBase.PREFIX_DEL:
        fm.delete(file);
        return;
    }
    getLog().error("Unreachable message:", mark_file, "[" + mark + "]");
  }


  /**
   * 从全局事件移除自身
   */
  public void removeModifyListener() {
    boolean rm = GlobalEventBus.me().off(Names.ui_file_change, this);
    assert rm : "must removed";
  }
}
