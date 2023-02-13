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
// 文件创建日期: 17-12-8 上午8:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Page.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.script.IVisitByScript;


public class Page implements IVisitByScript {

  public final static int PAGE_DEFAULT_COUNT = -1;


  /** 从 0 开始的页码 */
  public int pageNum;
  /** 一页行数 */
  public int pageSize;
  /** 总行数 */
  public int totalCount;
  /** 从 0 开始的偏移 */
  public int offset;


  /**
   * 分页数据
   * @param pageNum 从 1 开始的页码
   * @param pageSize
   * @param totalCount
   */
  public Page(int pageNum, int pageSize, int totalCount) {
    if (pageNum < 1)
      throw new XBosonException.BadParameter(
              "int pageNum", "Should be greater than 0");

    if (pageSize < 1)
      throw new XBosonException.BadParameter(
              "int pageSize", "Should be greater than 0");

    if (totalCount > 0) {
      this.totalCount = totalCount;
    } else {
      this.totalCount = -1;
    }

    this.pageNum = pageNum - 1;
    this.pageSize = pageSize;
    this.offset = this.pageNum * this.pageSize;
  }
}
