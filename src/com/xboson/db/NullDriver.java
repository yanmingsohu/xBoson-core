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
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/NullDriver.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

import com.xboson.been.Page;


/**
 * 方言的默认实现, 都会抛出异常
 */
public abstract class NullDriver implements IDialect, IDriver {

  static final String NOW_SQL = "select now() " + NOW_TIME_COLUMN;
  static final String COUNT_SQL_0 = "Select count(1) AS "
          + TOTAL_SIZE_COLUMN +" From ( ";
  static final String COUNT_SQL_1 = " ) cnt_tbl";


  @Override
  public String nowSql() {
    return NOW_SQL;
  }


  public String createCatalog(String name) {
    throw new UnsupportedOperationException("Unsupported Create Catalog");
  }


  @Override
  public String count(String selectSql) {
    return COUNT_SQL_0+ selectSql + COUNT_SQL_1;
  }


  @Override
  public String limitResult(String selectSql, Page page) {
    throw new UnsupportedOperationException("Unsupported Limit Result");
  }
}
