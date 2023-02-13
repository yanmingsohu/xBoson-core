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
// 文件创建日期: 2017年11月5日 下午2:32:09
// 原始文件路径: xBoson/src/com/xboson/script/IScriptFileSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.script;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 每个机构的每个应用都有一个虚拟文件系统,
 * 这个接口对文件整个进行操作, 不适合大文件, 但适合脚本应用文件.
 * 这个接口被设计为 Api Script 专用.
 */
public interface IScriptFileSystem {

  /**
   * 读取路径上的文件, 返回文件内容, 如果文件不存在返回 null,
   * 只有在出现异常的情况才应该抛出 IOException
   */
  ByteBuffer readFile(String path) throws IOException;


  /**
   * 读取文件属性
   */
  ScriptAttr readAttribute(String path) throws IOException;


  /**
   * 返回文件系统的id, 不同机构的id不同
   */
  String getID();


  /**
   * 返回文件系统类型
   */
  String getType();

}
