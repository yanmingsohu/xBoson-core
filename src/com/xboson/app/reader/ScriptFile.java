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
// 文件创建日期: 17-12-16 上午10:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/reader/ScriptFile.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.reader;

import com.xboson.fs.script.ScriptAttr;
import com.xboson.util.Hash;


public class ScriptFile {

  /**
   * 文件属性
   */
  public final ScriptAttr attr;

  /**
   * 打过补丁的源代码
   */
  public final byte[] content;


  /**
   * 未打补丁未解密的原始文件
   */
  public final String original_code;


  /**
   * original_code 的 hash 值
   */
  public final String original_hash;


  /**
   * 脚本文件
   *
   * @param content 脚本文件打过补丁的源代码
   * @param orgi 未打过补丁使用 zip=0 加密的原文件
   */
  public ScriptFile(byte[] content, String orgi, ScriptAttr attr) {
    this.content = content;
    this.attr = attr;
    this.original_code = orgi;

    Hash h = new Hash();
    h.update(original_code);
    this.original_hash = h.digestStr();
  }

}
