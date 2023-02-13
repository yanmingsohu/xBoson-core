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
   * 在集群上使用抛出异常.
   */
  String scriptLoad(String script);


  /**
   * 预加载脚本, 该方法可用在单机/集群模式使用.
   * @param script 脚本文本
   * @param key 驱动根据该值确定连接的集群节点, 单机模式忽略该参数
   * @return 返回脚本句柄
   */
  String scriptLoad(final String script, final String key);


  /**
   * 脚本已经预加载返回 true, 在集群上会抛出异常
   */
  Boolean scriptExists(String sha1);


  /**
   * 该方法可用在单机/集群模式使用, 返回脚本状态
   * @param sha1 脚本句柄
   * @param key 驱动根据该值确定连接的集群节点, 单机模式忽略该参数
   * @return 脚本存在返回 true
   */
  Boolean scriptExists(final String sha1, final String key);


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
