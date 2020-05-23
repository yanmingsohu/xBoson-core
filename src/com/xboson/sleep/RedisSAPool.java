////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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

import java.util.concurrent.atomic.AtomicReference;


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
