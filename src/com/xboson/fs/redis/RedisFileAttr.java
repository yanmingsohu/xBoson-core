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
// 文件创建日期: 17-12-17 下午12:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/RedisFileAttr.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.fs.basic.AbsFileAttr;
import com.xboson.script.lib.Path;
import com.xboson.util.c0nst.IConstant;

import java.io.Serializable;


/**
 * 虽然也是文件属性类, 但是为 UI 存储优化.
 * file_content 可以能为 null.
 */
public class RedisFileAttr extends AbsFileAttr implements Serializable {

  private transient byte[] file_content;
  private transient boolean need_synchronization;


  private RedisFileAttr(String path, int type, long lastModify) {
    super(path, type, lastModify);
  }


  protected RedisFileAttr(RedisFileAttr fs) {
    super(fs);
    this.file_content = fs.file_content;
    this.need_synchronization = fs.need_synchronization;
  }


  public static RedisFileAttr createFile(String path,
                                         long lastModify,
                                         byte[] content)
  {
    RedisFileAttr fs = new RedisFileAttr(path, T_FILE, lastModify);
    fs.file_content = content;
    return fs;
  }


  public static RedisFileAttr createDir(String path) {
    RedisFileAttr fs = new RedisFileAttr(path, T_DIR, 0);
    return fs;
  }


  public byte[] getFileContent() {
    if (type != T_FILE) {
      throw new BadPath("Is not file");
    }
    return file_content;
  }


  public String getContentToString() {
    return new String(file_content, IConstant.CHARSET);
  }


  public void setFileContent(byte[] c) {
    file_content = c;
  }


  /**
   * 返回父路径字符串, 如果已经是父路径返回 null
   */
  public String parentPath() {
    return Path.me.dirname(path);
  }


  /**
   * 复制所有属性, 除了 path 只保留文件名部分,
   * 返回的对象中, 内容属性将指向同一个可变对象.
   */
  public final RedisFileAttr cloneBaseName() {
    String basename = Path.me.basename(path);
    RedisFileAttr fs = new RedisFileAttr(basename, type, lastModify);
    if (dir_contain != null) fs.dir_contain.addAll(dir_contain);
    fs.file_content = file_content;
    fs.need_synchronization = need_synchronization;
    return fs;
  }


  /**
   * 克隆所有属性除了 path 使用 newPath
   */
  public final RedisFileAttr cloneWithName(String newPath) {
    RedisFileAttr fs = new RedisFileAttr(newPath, type, lastModify);
    if (dir_contain != null) fs.dir_contain.addAll(dir_contain);
    fs.file_content = file_content;
    fs.need_synchronization = need_synchronization;
    return fs;
  }


  /**
   * 返回创建该对象的 IRedisFileSystemProvider 实例 id 值, 可由子类重写
   */
  public int mappingID() {
    return RedisFileMapping.ID;
  }


  public boolean needSynchronization() {
    return need_synchronization;
  }


  public void setSynchronization(boolean need) {
    need_synchronization = need;
  }
}
