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
// 文件创建日期: 17-11-14 上午10:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/IDialect.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

import com.xboson.been.Page;


/**
 * 方言接口, 定义各种方言语句
 */
public interface IDialect {

  String TOTAL_SIZE_COLUMN = "total_size";
  String NOW_TIME_COLUMN = "_now_";


  /**
   * 返回 SQL 文, 用来查询数据库服务器的当前时间, 列名为 _now_
   */
  String nowSql();


  /**
   * 返回创建 Catalog 的 sql 文, 没有参数绑定.
   * Catalog 在 mysql 中是 schema, 在 oracle 中是表空间.
   */
  String createCatalog(String name);


  /**
   * 将带有 select 的查询语句转换为返回一行 total_size 列的语句,
   * total_size 列返回 selectSql 查询实际返回多少行
   *
   * @param selectSql - 带有 select 的 sql 文.
   * @return 转换后的 sql 文
   */
  String count(String selectSql);


  /**
   * 限制结果集的返回行数, 包装 selectSql 后
   *
   * @param selectSql 带有 select 的 sql 文.
   * @param page 对结果集的限制
   * @return 转换后的 sql 文
   */
  String limitResult(String selectSql, Page page);

}
