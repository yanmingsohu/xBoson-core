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
// 文件创建日期: 17-11-13 下午3:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/FileSystemFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.script;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个文件系统的汇聚, 可以来自本地磁盘/网络磁盘/DB表/Redis缓存,
 * 凡是注册过的文件系统, 都可以再通过 open 打开.
 */
public class FileSystemFactory {

  private static FileSystemFactory instance;
  public static FileSystemFactory me() {
    if (instance == null) {
      synchronized (FileSystemFactory.class) {
        if (instance == null) {
          instance = new FileSystemFactory();
        }
      }
    }
    return instance;
  }


  private Log log;
  private Map<String, IScriptFileSystem> fss;


  private FileSystemFactory() {
    this.log = LogFactory.create();
    this.fss = new HashMap<>();
  }


  /**
   * 打开文件系统
   */
  public IScriptFileSystem open(String org, String app) {
    return open(org + app);
  }


  public IScriptFileSystem open(String id) {
    return fss.get(id);
  }


  /**
   * 将本地路径映射到一个文件系统中
   */
  public void addLocalFileSystem(String path, String id) {
    LocalFileSystem lfs = new LocalFileSystem(path, id);
    fss.put(id, lfs);
  }


  public void addLocalFileSystem(URL url, String id) throws URISyntaxException {
    File f = new File(url.toURI());
    addLocalFileSystem(f.getPath(), id);
  }
}
