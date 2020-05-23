////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-5-23 上午10:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/IRedis.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;


import redis.clients.jedis.*;

import java.io.Closeable;


/**
 * 实现该接口的可能是单机或集群
 */
public interface IRedis extends JedisCommands, Closeable {

  /**
   * 如果是集群连接, 返回 true
   */
  boolean isCluster();
  void close();


  /**
   * 运行预加载的脚本
   * @param sha1 使用 scriptLoad 加载脚本后返回的脚本句柄
   * @param keyCount
   * @param params
   * @return 脚本的运行结果
   */
  Object evalsha(String sha1, int keyCount, String... params);


  /**
   * 使 redis 预加载脚本, 返回脚本的 sha1 用于引用这个脚本.
   * 当使用集群使, 脚本会因为节点与 key 不对应而抛出异常.
   */
  String scriptLoad(String script);


  /**
   * 脚本已经预加载返回 true
   */
  Boolean scriptExists(String sha1);


  /**
   * 清空 redis 上的所有脚本
   */
  String scriptFlush();


  /**
   * @see BinaryJedis#hset(byte[], byte[], byte[])
   */
  Long hset(final byte[] key, final byte[] field, final byte[] value);


  /**
   * @see BinaryJedis#hget(byte[], byte[])
   */
  byte[] hget(final byte[] key, final byte[] field);


  /**
   * @see BinaryJedis#hdel(byte[], byte[]...)
   */
  Long hdel(final byte[] key, final byte[]... fields);


  /**
   * 切换数据库, 这在集群上不可用
   */
  String select(final int index);


  /**
   * 返回当前数据库 id
   */
  Long getDB();


  /**
   * 返回事务对象, 在集群上不可用并抛出异常
   */
  Transaction multi();


  Long publish(String channel, String message);

  void subscribe(JedisPubSub jedisPubSub, String... channels);

  void psubscribe(JedisPubSub jedisPubSub, String... patterns);
}
