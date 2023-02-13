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
// 文件创建日期: 17-11-14 上午10:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/driver/SqlServer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.driver;

import com.xboson.been.Page;
import com.xboson.db.ConnectConfig;
import com.xboson.db.IDriver;
import com.xboson.db.NullDriver;
import com.xboson.db.analyze.SqlParser;
import com.xboson.db.analyze.SqlParserCached;


public class SqlServer extends NullDriver implements IDriver {

  @Override
  public String driverClassName() {
    return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  }


  @Override
  public String name() {
    return "sqlserver";
  }


  @Override
  public int id() {
    return 2;
  }


  @Override
  public String getUrl(ConnectConfig config) {
    return "jdbc:sqlserver://"
            + config.getHost() + ':'
            + config.getPort() + "; DatabaseName="
            + config.getDatabase();
  }


  @Override
  public int port() {
    return 1433;
  }


  @Override
  public String nowSql() {
    return "select getdate() _now_";
  }


  @Override
  public String createCatalog(String name) {
    return "CREATE DATABASE " + name;
  }


  @Override
  public String limitResult(String sql, Page page) {
    SqlParserCached.ParsedDataHandle handle = SqlParserCached.parse(sql);
    String tableName = SqlParser.orderOrName(handle);
    StringBuilder out = new StringBuilder(sql);

    if (tableName != null) {
      out.append(" Order By ");
      out.append(tableName);
    }
    out.append(" OFFSET ");
    out.append(page.offset);
    out.append(" ROWS FETCH NEXT ");
    out.append(page.pageSize + page.offset);
    out.append(" ROWS ONLY");
    return out.toString();
  }
}
