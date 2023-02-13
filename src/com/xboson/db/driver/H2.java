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
// 文件创建日期: 17-11-14 下午12:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/driver/H2.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db.driver;

import com.xboson.been.Page;
import com.xboson.db.ConnectConfig;
import com.xboson.db.NullDriver;
import com.xboson.util.SysConfig;


public class H2 extends NullDriver {

  private final String baseDir;


  public H2() {
    baseDir = SysConfig.me().readConfig().configPath + "/h2-db/";
  }

  @Override
  public String driverClassName() {
    return "org.h2.Driver";
  }


  @Override
  public String name() {
    return "h2";
  }


  @Override
  public int id() {
    return 20;
  }


  @Override
  public String getUrl(ConnectConfig config) {
    String local_db_file = baseDir + config.getDatabase();
    return "jdbc:h2:file:"+ local_db_file +";CIPHER=AES;";
  }


  @Override
  public int port() {
    return 0;
  }


  @Override
  public String limitResult(String sql, Page page) {
    return Mysql.limit(sql, page);
  }
}
