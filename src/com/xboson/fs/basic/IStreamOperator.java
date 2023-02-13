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
// 文件创建日期: 18-1-3 下午2:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/basic/IStreamOperator.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.basic;

import java.io.InputStream;
import java.io.OutputStream;


public interface IStreamOperator<ATTR extends IFileAttribute>
        extends IFileOperatorBase<ATTR> {

  /**
   * 打开文件的输入流, 从文件读取数据.
   */
  InputStream openInputStream(String file);


  /**
   * 打开文件的输出流, 向文件写入数据.
   */
  OutputStream openOutputStream(String file);

}
