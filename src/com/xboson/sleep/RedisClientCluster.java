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
// 文件创建日期: 20-5-22 下午7:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/RedisClientCluster.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.been.XBosonException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Transaction;


public class RedisClientCluster extends JedisCluster implements IRedis {


  RedisClientCluster(HostAndPort node, int connectionTimeout, int soTimeout,
                     int maxAttempts, String password,
                     final GenericObjectPoolConfig pc) {
    super(node, connectionTimeout, soTimeout, maxAttempts, password, pc);
  }


  @Override
  public void close() {}


  @Override
  public boolean isCluster() {
    return true;
  }


  @Override
  public String scriptLoad(String script) {
    throw new XBosonException.NotImplements(
            "MOVD scriptLoad(final String script, final String key)");
  }


  @Override
  public Boolean scriptExists(String sha1) {
    throw new XBosonException.NotImplements(
            "MOVD scriptExists(final String sha1, final String key)"
    );
  }


  @Override
  public String scriptFlush() {
    throw new XBosonException.NotImplements();
  }


  @Override
  public Transaction multi() {
    throw new XBosonException.NotImplements();
  }

}
