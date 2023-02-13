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
// 文件创建日期: 17-11-19 上午9:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/ConfigCoreDB.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.db.ConnectConfig;
import com.xboson.db.DbmsFactory;
import com.xboson.db.IDriver;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.Tool;

import java.sql.Connection;


public class ConfigCoreDB implements IStep {

  @Override
  public int order() {
    return 3;
  }


  @Override
  public boolean gotoNext(HttpData data) {
    ConnectConfig db = data.cf.db;
    db.setHost(data.req.getParameter("host"));
    db.setPort(data.req.getParameter("port"));
    db.setDbname(data.req.getParameter("dbname"));
    db.setUsername(data.req.getParameter("username"));
    db.setPassword(data.req.getParameter("password"));
    db.setDatabase("");

    String catalog = data.req.getParameter("database");
    if (Tool.isNulStr(catalog)) {
      data.msg = "必须指定 database/catalog/schema 名称";
      return false;
    }

    String createdb = data.req.getParameter("createdb");
    boolean autocreate = createdb != null && "1".equals(createdb);

    try (Connection conn = DbmsFactory.me().openWithoutPool(db)) {
      try {
        conn.setCatalog(catalog);
      } catch (Exception e) {
        if (!autocreate)
          throw e;

        IDriver d = DbmsFactory.me().getDriver(db);
        String sql = d.createCatalog(catalog);
        conn.createStatement().execute(sql);

        data.msg = "创建了 " + catalog;
        return false;
      }
      db.setDatabase(catalog);
      return true;
    } catch(Exception e) {
      data.msg = e.getMessage();
      e.printStackTrace();
    }
    return false;
  }


  @Override
  public String getPage(HttpData data) {
    return "db.jsp";
  }
}
