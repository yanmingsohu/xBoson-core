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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/basic/IFileAttribute.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.basic;

/**
 * 不对文件属性做任何约定
 */
public interface IFileAttribute {

  /** 文件 */
  int T_FILE = 1;
  /** 目录 */
  int T_DIR  = 2;

  /**
   * 返回当前路径上对象的类型 (文件/目录/其他)
   */
  int type();


  /**
   * 返回规范化的绝对路径
   */
  String path();

}
