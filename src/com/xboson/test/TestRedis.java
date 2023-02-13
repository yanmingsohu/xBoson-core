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
// 文件创建日期: 17-11-11 上午11:18
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestRedis.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.SessionData;
import com.xboson.sleep.RedisMesmerizer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;

import static javax.swing.UIManager.get;


public class TestRedis extends Test {

  public void test() throws Exception {
    test_pool();
    test_remove_all();
  }


  public void test_remove_all() throws Exception {
    sub("RedisMesmerizer removeAll");
    SessionData sd = new SessionData();
    RedisMesmerizer.me().removeAll(sd);
  }


  public void test_pool() throws Exception {
    sub("Test Pool");

    JedisPoolConfig config = new JedisPoolConfig();
    JedisPool pool = new JedisPool(config, "localhost");

    try (Jedis client = pool.getResource()) {
      String random = randomString(100);
      client.set("test", random);

      String b = client.get("test");
      msg("String: " + random);
      ok(random.equals(b), "get / set");

      byte[] c1 = randomBytes(100);
      byte[] n = "bin".getBytes();
      client.set(n, c1);
      byte[] c2 = client.get(n);
      ok(Arrays.equals(c1, c2), "bin data");
    }

    pool.destroy();
  }


  public static void main(String[] a) throws Exception {
    new TestRedis();
  }
}
