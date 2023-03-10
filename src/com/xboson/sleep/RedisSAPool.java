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
// 文件创建日期: 20-5-24 上午7:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/RedisSAPool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;


public class RedisSAPool extends JedisPool {

  public static final String CLIENT_NAME = "xBoson_Core-use_Jedis";

  private String host;
  private int port;
  private String password;
  private int timeout;
  private int database;


  class Factory implements PooledObjectFactory<Jedis> {

    @Override
    public PooledObject<Jedis> makeObject() throws Exception {
      RedisClientStandAlone jedis =
              new RedisClientStandAlone(host, port, timeout);

      try {
        jedis.connect();

        if (null != password) {
          jedis.auth(password);
        }
        if (database != 0) {
          jedis.select(database);
        }

        jedis.clientSetname(CLIENT_NAME);
      } catch (JedisException je) {
        jedis.close();
        throw je;
      }
      return new DefaultPooledObject<Jedis>(jedis);
    }


    @Override
    public void destroyObject(PooledObject<Jedis> pooledObject) throws Exception {
      final BinaryJedis jedis = pooledObject.getObject();
      if (jedis.isConnected()) {
        try {
          try {
            jedis.quit();
          } catch (Exception e) {
          }
          jedis.disconnect();
        } catch (Exception e) {
        }
      }
    }


    @Override
    public boolean validateObject(PooledObject<Jedis> pooledObject) {
      final BinaryJedis jedis = pooledObject.getObject();
      try {
        return jedis.ping().equals("PONG");
      } catch (final Exception e) {
        return false;
      }
    }


    @Override
    public void activateObject(PooledObject<Jedis> pooledObject) throws Exception {
      final BinaryJedis jedis = pooledObject.getObject();
      if (jedis.getDB() != database) {
        jedis.select(database);
      }
    }


    @Override
    public void passivateObject(PooledObject<Jedis> pooledObject) throws Exception {
      // nothing
    }
  }


  public RedisSAPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port,
                     int timeout, final String password) {
    this.host = host;
    this.port = port;
    this.password = password;
    this.timeout = timeout;
    this.database = RedisMesmerizer.DEFAULT_DATABASE;
    super.initPool(poolConfig, new Factory());
  }


  public IRedis openClient() {
    return (IRedis) super.getResource();
  }

}
