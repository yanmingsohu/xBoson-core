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
// 文件创建日期: 17-11-19 上午9:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/install/step/ConfigRedis.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init.install.step;

import com.xboson.been.RedisConfig;
import com.xboson.db.ConnectConfig;
import com.xboson.init.install.HttpData;
import com.xboson.init.install.IStep;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;


public class ConfigRedis implements IStep {

  @Override
  public int order() {
    return 4;
  }


  @Override
  public boolean gotoNext(HttpData data) {
    RedisConfig redis = data.cf.redis;
    redis.setHost(data.req.getParameter("rhost"));
    redis.setPort(data.req.getParameter("rport"));
    redis.setPassword(data.req.getParameter("rpassword"));

    final int port = redis.getIntPort(6379);
    final String host = redis.getHost();

    try (Jedis jc = new Jedis(host, port)) {
      if (!Tool.isNulStr(redis.getPassword())) {
        jc.auth(redis.getPassword());
      }
      jc.ping();
      return true;

    } catch(Exception e) {
      data.msg = e.getMessage();
      e.printStackTrace();
    }

    return false;
  }


  @Override
  public String getPage(HttpData data) {
    return "redis.jsp";
  }
}
