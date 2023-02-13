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
// 文件创建日期: 18-1-3 下午2:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/MongoConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collections;
import java.util.List;


/**
 * 创建客户端需要的配置
 */
public class MongoConfig {

  public static final int TIMEOUT = 3000;

  public String host;
  public int port;
  public String database;
  public String username;
  public String password;
  public boolean enable;


  public ServerAddress address() {
    return new ServerAddress(host, port);
  }


  public List<MongoCredential> credential() {
    char[] ps;
    if (password != null) {
      ps = password.toCharArray();
    } else if (username == null) {
      return Collections.emptyList();
    } else {
      ps = null;
    }
    return Collections.singletonList(
            MongoCredential.createCredential(username, database, ps));
  }


  public MongoClientOptions options() {
    MongoClientOptions.Builder opt = MongoClientOptions.builder();
    opt.connectTimeout(TIMEOUT);
    opt.serverSelectionTimeout(TIMEOUT);
    return opt.build();
  }
}
