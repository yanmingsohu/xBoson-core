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
// 文件创建日期: 18-1-3 上午10:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/basic/IBlockOperator.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.basic;

import com.xboson.been.XBosonException;
import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import com.xboson.util.c0nst.IConstant;


public interface IBlockOperator<ATTR extends IFileAttribute>
        extends IFileOperatorBase<ATTR> {

  /**
   * 快速读取文件内容, 不推荐使用; 尝试读取目录会抛出异常.
   * 应该使用 readAttribute()/readFileContent() 的组合来读取文件.
   *
   * @param path 路径
   * @return 文件的字节内容, 文件不存在返回 null
   * @throws XBosonException.IOError
   */
  byte[] readFile(String path);


  /**
   * 修改文件/创建文件, 同时会改变文件的修改时间;
   * 如果文件的路径中包含不存在的目录, 必要时会自动生成这些目录.
   *
   * @param path 文件
   * @param bytes 文件内容.
   * @throws XBosonException.IOError
   */
  void writeFile(String path, byte[] bytes);


  /**
   * 将 js 中的 Buffer 写入文件.
   */
  default void writeFile(String path, Buffer.JsBuffer buf) {
    writeFile(path, buf._buffer().array());
  }


  default void writeFile(String path, Bytes buf) {
    writeFile(path, buf.bin());
  }


  default void writeFile(String path, String buf) {
    writeFile(path, buf.getBytes(IConstant.CHARSET));
  }
}
