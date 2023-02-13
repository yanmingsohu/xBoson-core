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
// 文件创建日期: 18-1-4 上午9:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/basic/IFinder.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.basic;

/**
 * 文件搜索操作接口
 * @param <Result> 搜索结果集
 */
public interface IFinder<Result> {

  /**
   * 模糊查询符合路径的完整路径集合, 总是大小写敏感的, 自行添加匹配模式.
   */
  Result findPath(String pathName);


  /**
   * 查询文件内容, 返回文件列表
   *
   * @param basePath 开始目录
   * @param content 要搜索的文本
   * @param cs true 则启用大小写敏感
   */
  Result findContent(String basePath, String content, boolean cs);

}
