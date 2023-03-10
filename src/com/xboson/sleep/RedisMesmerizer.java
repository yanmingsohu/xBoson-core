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
// 文件创建日期: 17-11-11 下午12:54
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/RedisMesmerizer.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.been.Config;
import com.xboson.been.RedisConfig;
import com.xboson.been.XBosonException;
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Hex;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import redis.clients.jedis.*;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;


/**
 * 能正确处理 json/bin 类型
 * 如果收到 sleep 发出的异常, 可能是实例内部类无法正确序列化导致的.
 *
 * @see IBinData 二进制持久化, 适合 java 对象
 * @see IJsonData json 字符串持久化
 * @see ITimeout 持久化超时
 */
public class RedisMesmerizer extends OnExitHandle implements IMesmerizer {

  public final static String KEY = "RedisMesmerizer.IMesmerizer";
  /** 对于游标, 开始和结束值 */
  public final static String BEGIN_OVER_CURSOR = "0";
  public final static String BINARY = "BIN";
  public final static String JSON = "JSON";
  public final static int TIMEOUT = 3000;
  public final static int SoTIMEOUT = 2000;
  public final static int MaxAttempts = 5;
  public final static int DEFAULT_DATABASE = 0;

  private static RedisMesmerizer instance;

  private Log log = LogFactory.create();
  private RedisClientCluster jcluster;
  private RedisSAPool jpool;
  private JSON json;
  private BIN bin;
  private final boolean cluster;


  public static RedisMesmerizer me() {
    if (instance == null) {
      synchronized (RedisMesmerizer.class) {
        if (instance == null) {
          instance = new RedisMesmerizer();
        }
      }
    }
    return instance;
  }


  private RedisMesmerizer() {
    json = new JSON();
    bin = new BIN();

    Config config = SysConfig.me().readConfig();
    JedisPoolConfig pool = config.jedispool;
    RedisConfig redis = config.redis;
    cluster = redis.isCluster();

    final String ps = redis.getPassword(null);
    final String host = redis.getHost("localhost");
    final int port = redis.getIntPort(6379);

    if (cluster) {
      jcluster = new RedisClientCluster(new HostAndPort(host, port),
                TIMEOUT, SoTIMEOUT, MaxAttempts, ps, pool);
    } else {
      jpool = new RedisSAPool(pool, host, port, TIMEOUT, ps);
    }

    log.info("Initialization Success");
  }


  /**
   * 打开 redis 客户端连接, 用完记得关闭
   */
  public IRedis open() {
    if (cluster) {
      return jcluster;
    } else {
      return jpool.openClient();
    }
  }


  @Override
  protected void exit() {
    jpool.destroy();
    jpool = null;
    jcluster = null;
  }


  /**
   * 计算持久化时使用的 ID
   */
  static String genid(ISleepwalker sw, String type) {
    String id = sw.getid();
    return "/" + type + "/" + sw.getClass().getName() + "/" + id;
  }


  public static String genid(Class clazz, String id, String type) {
    return "/" + type + "/" + clazz.getName() + "/" + id;
  }


  @Override
  public void sleep(ISleepwalker data) {
    if (data instanceof IBinData) {
      bin.sleep(genid(data, BINARY), data);
    }
    else {
      json.sleep(genid(data, JSON), data);
    }
  }


  @Override
  public ISleepwalker wake(Class<? extends ISleepwalker> c, String id) {
    if (IBinData.class.isAssignableFrom(c)) {
      return bin.wake(c, genid(c, id, BINARY));
    }
    else {
      return json.wake(c, genid(c, id, JSON));
    }
  }


  public void remove(ISleepwalker data) {
    if (data instanceof IBinData) {
      bin.remove(data);
    }
    else {
      json.remove(data);
    }
  }


  public void removeAll(ISleepwalker data) {
    String id;
    if (data instanceof IBinData) {
      id = genid(data.getClass(), "*", BINARY);
    }
    else {
      id = genid(data.getClass(), "*", JSON);
    }

    try (IRedis client = open()) {
      ScanParams sp = new ScanParams();
      sp.match(id);
      String cursor = BEGIN_OVER_CURSOR;

      for (;;) {
        ScanResult<Map.Entry<String, String>>
                sr = client.hscan(KEY, cursor, sp);

        Iterator<Map.Entry<String, String>> it = sr.getResult().iterator();
        while (it.hasNext()) {
          Map.Entry<String, String> item = it.next();
          String itemkey = item.getKey();
          //
          // 可以用 string 类型的 key 删除 byte[] 类型的 key
          //
          client.hdel(KEY, itemkey);
        }

        cursor = sr.getStringCursor();
        if (cursor.equals(BEGIN_OVER_CURSOR))
          break;
      }
    }
  }


  /**
   * 创建一个执行对象, 用来清除符合 data 类型的所有数据
   */
  public TimerTask createCleanTask(final ISleepwalker data) {
    return new TimerTask() {
      public void run() {
        removeAll(data);
      }
    };
  }


  /**
   * JSON 持久化
   */
  class JSON {
    public void sleep(String id, Object data) {
      try (IRedis client = open()) {
        if (check_timeout(client, data, id))
          return;

        String str = Tool.getAdapter((Class) data.getClass()).toJson(data);
        client.hset(KEY, id, str);
      }
    }

    public ISleepwalker wake(Class c, String id) {
      try (IRedis client = open()) {
        String str = client.hget(KEY, id);
        if (str == null) {
          return null;
        }

        ISleepwalker obj = (ISleepwalker) Tool.getAdapter(c).fromJson(str);
        if (check_timeout(client, obj, id))
          return null;

        return obj;
      } catch(IOException e) {
        log.debug("JSON.wake", e);
        throw new XBosonException(e);
      }
    }

    public void remove(ISleepwalker data) {
      try (IRedis client = open()) {
        String id = genid(data, JSON);
        client.hdel(KEY, id);
      }
    }

    private boolean check_timeout(IRedis client, Object obj, String id) {
      if (obj instanceof ITimeout) {
        ITimeout to = (ITimeout) obj;
        if (to.isTimeout()) {
          client.hdel(KEY, id);
          return true;
        }
      }
      return false;
    }
  }


  /**
   * Java 序列化
   */
  class BIN {
    byte [] KEY_BYTE = KEY.getBytes();

    public void sleep(String id, Object data) {
      try (IRedis client = open()) {
        byte[] bid = id.getBytes();
        if (check_timeout(client, data, bid))
          return;

        byte[] out = Hex.toBytes(data);
        client.hset(KEY_BYTE, bid, out);
      } catch(IOException e) {
        log.debug("BIN.sleep", e);
        throw new XBosonException(e);
      }
    }

    public IBinData wake(Class c, String id) {
      try (IRedis client = open()) {
        byte[] bid  = id.getBytes();
        byte[] data = client.hget(KEY_BYTE, bid);
        if (data == null) {
          return null;
        }

        IBinData obj = null;
        try {
          obj = (IBinData) Hex.fromBytes(data);
        } catch(InvalidClassException ice) {
          log.debug("BIN.wake", ice);
          //
          // 反序列化失败, 比如类定义改变, 序列号改变.
          //
          client.hdel(KEY_BYTE, bid);
          return null;
        }

        if (check_timeout(client, obj, bid))
          return null;

        if (obj.getClass() != c)
          throw new XBosonException.BadParameter("Is not", c.getName());

        return obj;
      } catch(IOException|ClassNotFoundException e) {
        log.debug("BIN.wake", e);
        throw new XBosonException(e);
      }
    }

    public void remove(ISleepwalker data) {
      try (IRedis client = open()) {
        String id = genid(data, BINARY);
        client.hdel(KEY_BYTE, id.getBytes());
      }
    }

    private boolean check_timeout(IRedis client, Object obj, byte[] bid) {
      if (obj instanceof ITimeout) {
        ITimeout to = (ITimeout) obj;
        if (to.isTimeout()) {
          client.hdel(KEY_BYTE, bid);
          return true;
        }
      }
      return false;
    }
  }
}
