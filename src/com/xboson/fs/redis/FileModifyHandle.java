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
// 文件创建日期: 17-11-20 上午10:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/FileModifyHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.been.XBosonException;
import com.xboson.event.GLHandle;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.Names;
import com.xboson.util.Tool;

import javax.naming.event.NamingEvent;


/**
 * 封装 UI 文件修改通知到接口
 */
public class FileModifyHandle extends GLHandle {

  private IFileChangeListener fm;
  private final String fileChangeEventName;


  /**
   * FileModifyHandle 创建后会启动事件迁移线程
   */
  public FileModifyHandle(IFileChangeListener fm, IFileSystemConfig config) {
    if (fm == null)
      throw new XBosonException.NullParamException("IFileChangeListener fm");

    this.fm = fm;
    this.fileChangeEventName = config.configFileChangeEventName();
    GlobalEventBus.me().on(fileChangeEventName, this);
    config.startMigrationThread();
  }


  @Override
  public void objectChanged(NamingEvent namingEvent) {
    String mark_file = (String) namingEvent.getNewBinding().getObject();
    String file = mark_file.substring(1);
    char mark = mark_file.charAt(0);

    switch(mark) {
      case RedisBase.PREFIX_DIR:
        fm.noticeMakeDir(file);
        return;

      case RedisBase.PREFIX_FILE:
        fm.noticeModifyContent(file);
        return;

      case RedisBase.PREFIX_DEL:
        fm.noticeDelete(file);
        return;

      case RedisBase.PREFIX_MOVE:
        int i = file.indexOf(":");
        if (i <= 0)
          throw new XBosonException("Bad move event format");
        String src = file.substring(0, i);
        String to  = file.substring(i+1);
        fm.noticeMove(src, to);
        return;

      default:
        getLog().error("Unreachable message:",
                mark_file, "[" + mark + "]");
    }
  }


  /**
   * 从全局事件移除自身
   */
  public void removeModifyListener() {
    boolean rm = GlobalEventBus.me().off(fileChangeEventName, this);
    assert rm : "must removed";
  }
}
