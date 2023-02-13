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
// 文件创建日期: 18-1-22 下午12:57
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/ConfigMongoDB.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoIterable;
import com.xboson.been.MongoConfig;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;


public class ConfigMongoDB implements IStep {

  @Override
  public int order() {
    return 8;
  }


  @Override
  public boolean gotoNext(HttpData data) throws Exception {
    MongoConfig mdb = data.cf.mongodb;

    if (data.getBool("skip")) {
      mdb.enable = false;
      return true;
    }

    mdb.enable = true;
    mdb.host = data.getStr("host");
    mdb.port = data.getInt("port");
    mdb.username = data.getStr("username");
    mdb.password = data.getStr("password");
    mdb.database = data.getStr("database");

    try (MongoClient cli = new MongoClient(
            mdb.address(), mdb.credential(), mdb.options())) {
      MongoIterable<String> mi = cli.listDatabaseNames();
      return mi.first() != null;
    }
  }


  @Override
  public String getPage(HttpData data) {
    return "mangodb.jsp";
  }
}
