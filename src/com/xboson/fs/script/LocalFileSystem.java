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
// 文件创建日期: 17-11-13 下午3:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/LocalFileSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.script;

import com.xboson.app.fix.SourceFix;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.script.IVisitByScript;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * 将测试目录文件映射到虚拟目录
 */
public class LocalFileSystem extends FSHelper
        implements IScriptFileSystem, IVisitByScript {

  /** <virtual_filename, code> */
  private final Map<String, ByteBuffer> file_cache;
  private final String id;
  private final String basedir;
  private final Log log;
  private final FileSystem fs;


  public LocalFileSystem(String basedir, String id) {
    this.id = id;
    this.fs = FileSystems.getDefault();
    this.basedir = basedir;
    this.file_cache = new HashMap<>();
    this.log = LogFactory.create();
  }


  /**
   * 设定静态文件内容
   */
  public void putcode(String name, String code) {
    Path p = Paths.get(name);
    ByteBuffer buf = ByteBuffer.wrap(code.getBytes());
    file_cache.put(p.toString(), buf);
  }


  /**
   * 将虚拟路径映射的真实路径 (在 basedir 的基础上)
   */
  public ByteBuffer putfile(String virtual_name, String file) throws IOException {
    Path p = Paths.get(basedir, file);
    if (! Files.exists(p)) return null;
    byte[] content = Files.readAllBytes(p);
    content = SourceFix.autoPatch(content);
    ByteBuffer buf = ByteBuffer.wrap(content);
    file_cache.put(virtual_name, buf);
    // log.debug("PUT", basedir, virtual_name, "=>",
    //        file, new String(content));
    return buf;
  }


  @Override
  public ByteBuffer readFile(String path) throws IOException {
    Path p = Paths.get(path);
    ByteBuffer buf = file_cache.get(p.toString());
    //log.debug("READ", path);
    if (buf == null) {
      return putfile(path, path);
    }
    return buf;
  }


  @Override
  public ScriptAttr readAttribute(String path) throws IOException {
    Path p = Paths.get(basedir, path);
    BasicFileAttributes bfa = Files.readAttributes(p, BasicFileAttributes.class);
    ScriptAttr fa = new ScriptAttr(bfa, p);
    //log.debug("Attribute", path);
    return fa;
  }


  @Override
  public String getID() {
    return id;
  }


  public String getType() {
    return "LocalFileSystem";
  }

}
