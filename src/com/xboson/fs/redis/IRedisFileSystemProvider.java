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
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/fs/ui/IRedisFileSystemProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.been.XBosonException;
import com.xboson.fs.basic.IBlockOperator;
import com.xboson.fs.basic.IFinder;


/**
 * 对 ui 文件的操作, 接口尽可能简单, 每个属性都是分离的.
 * 所有的路径参数已经规范化, 不包含任何 "/./" 和 "/../", 并且使用 unix 分隔符,
 * 路径为 ui 虚拟目录, 跟目录应该包含 'ui' 't' 'web' 'lib' 等目录
 */
public interface IRedisFileSystemProvider extends
        IBlockOperator<RedisFileAttr>, IFinder<FinderResult> {


  /** 结果集最大数量, 超过后的数据被忽略 */
  int MAX_RESULT_COUNT = 30;


  /**
   * 读取文件内容, 目录会抛出异常
   * @param fs
   * @throws XBosonException.IOError
   */
  void readFileContent(RedisFileAttr fs);

}
