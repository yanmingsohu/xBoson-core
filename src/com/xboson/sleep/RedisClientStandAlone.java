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
// 文件创建日期: 20-5-23 上午10:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/RedisClientStandAlone.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import redis.clients.jedis.Jedis;


public class RedisClientStandAlone extends Jedis implements IRedis {

  public RedisClientStandAlone(final String host, final int port, final int timeout) {
    super(host, port, timeout, timeout,
            false, null, null, null);
  }


  @Override
  public boolean isCluster() {
    return false;
  }


  @Override
  public String scriptLoad(String script, String key) {
    return scriptLoad(script);
  }


  @Override
  public Boolean scriptExists(String sha1, String key) {
    return scriptExists(sha1);
  }
}
