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
// 文件创建日期: 17-11-15 下午2:59
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/watcher/RecursiveDirectoryTraversal.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.watcher;

import java.io.IOException;
import java.nio.file.WatchEvent;


/**
 * 递归的监视目录中的变动
 */
public class RecursiveDirectoryTraversal implements INotify {

  /**
   * !! 未实现
   */
  private RecursiveDirectoryTraversal() {}

  @Override
  public void notify(String basename, String filename,
                     WatchEvent event, WatchEvent.Kind kind)
          throws IOException {

  }


  @Override
  public void remove(String basename) {

  }
}
