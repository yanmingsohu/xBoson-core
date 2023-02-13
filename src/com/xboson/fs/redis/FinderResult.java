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
// 文件创建日期: 17-12-19 上午9:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/FinderResult.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.util.JavaConverter;

import java.util.Collections;
import java.util.List;


/**
 * 内容查询结果集, 数据不可变.
 *
 * 结果集通常有查询结果数量限制, 当到达该限制多出的数据被忽略, 并且没有方法将忽略的
 * 数据再次查询出来, 唯一的方法就是增加查询表达式的复杂度来收缩结果集数量, 这样的设
 * 计符合前端对于大量数据无意义的策略.
 */
public class FinderResult {

  /** 包含搜索内容的文件名列表(完整路径) */
  public final List<String> files;

  /** redis 中的键名 */
  public final String baseDir;

  /** 搜索内容字符串, 可能已经被重新为匹配表达式 */
  public final String find;

  /** 开启大小写敏感 */
  public final boolean caseSensitive;

  /** 还有更多结果没有返回 (需要增加搜索内容减小搜索范围) */
  public final boolean hasMore;


  /**
   * 来自 lua 结果集构造一个查询结果对象
   * @param arr lua 中的 list.
   */
  public FinderResult(Object arr) {
    List info     = (List) arr;
    files         = Collections.unmodifiableList((List) info.get(0));
    baseDir       = (String) info.get(1);
    find          = (String) info.get(2);
    caseSensitive = JavaConverter.toBool(info.get(3));
    hasMore       = JavaConverter.toBool(info.get(4));
  }


  public FinderResult(List<String> files, String baseDir,
                      String find, boolean caseSensitive, boolean hasMore) {
    this.files          = files;
    this.baseDir        = baseDir;
    this.find           = find;
    this.caseSensitive  = caseSensitive;
    this.hasMore        = hasMore;
  }


  @Override
  public String toString() {
    return "[ Find: '"+ find +"' "+
            (caseSensitive ? "Case sensitive" : "Case insensitive") +
            ", On '"+ baseDir +"', "+ files.size() +" files, "+
            (hasMore ? "And more files" : "No more") + " ]";
  }
}
