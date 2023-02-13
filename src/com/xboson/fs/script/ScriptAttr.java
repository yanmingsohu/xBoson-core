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
// 文件创建日期: 17-11-13 下午3:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ScriptAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.script;

import com.xboson.been.JsonHelper;
import com.xboson.fs.basic.IFileAttribute;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 文件属性对象
 */
public class ScriptAttr extends JsonHelper implements
        Serializable, IFileAttribute {

  /** 只有文件名本身 */
  public String fileName;
  public long createTime;
  public long modifyTime;
  public String creatorUID;
  /** 只包含路径 */
  public String pathName;
  /** 路径 + 文件名 */
  public String fullPath;
  /** 文件的字节大小 */
  public long fileSize;


  public ScriptAttr() {}


  public ScriptAttr(BasicFileAttributes basic, Path fullpath) {
    this.fileName   = fullpath.getFileName().toString();
    this.createTime = basic.creationTime().toMillis();
    this.modifyTime = basic.lastModifiedTime().toMillis();
    this.creatorUID = null;
    this.pathName   = fullpath.getParent().toString();
    this.fullPath   = fullpath.toString();
    this.fileSize   = basic.size();
  }


  /**
   * 脚本文件系统没有目录
   */
  @Override
  public int type() {
    return T_FILE;
  }


  @Override
  public String path() {
    return fullPath;
  }
}
