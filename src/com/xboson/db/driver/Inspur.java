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
// 文件创建日期: 17-11-14 上午10:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/driver/Inspur.java
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


public class Inspur extends NullDriver implements IDriver {

  @Override
  public String driverClassName() {
    return "com.inspur.jdbc.KdDriver";
  }


  @Override
  public String name() {
    return "inspur";
  }


  @Override
  public int id() {
    return 6;
  }


  @Override
  public String getUrl(ConnectConfig config) {
    return "jdbc:inspur:thin:@"
            + config.getHost() + ':'
            + config.getPort() + ':'
            + config.getDatabase();
  }


  @Override
  public int port() {
    return 11229;
  }


  @Override
  public String nowSql() {
    return "SELECT SYSDATE _now_ FROM DUAL";
  }


  @Override
  public String limitResult(String selectSql, Page page) {
    return Oracle.limit(selectSql, page);
  }
}
