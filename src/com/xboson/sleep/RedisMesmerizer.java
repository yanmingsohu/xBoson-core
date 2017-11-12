////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import com.xboson.event.OnExitHandle;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 能正确处理 json/bin 类型
 */
public class RedisMesmerizer extends OnExitHandle implements IMesmerizer {

  private final static String KEY = "RedisMesmerizer.IMesmerizer";
  private static RedisMesmerizer instance;

  private Log log = LogFactory.create();
  private JedisPool jpool;
  private JSON json;
  private BIN bin;


  public synchronized static RedisMesmerizer me() {
    if (instance == null) {
      instance = new RedisMesmerizer();
    }
    return instance;
  }


  private RedisMesmerizer() {
    json = new JSON();
    bin = new BIN();

    Config config = SysConfig.me().readConfig();
    jpool = new JedisPool(config.jedispool, config.redis_host);
    log.info("Initialization Success");
  }


  /**
   * 打开 redis 客户端连接, 用完记得关闭
   */
  public Jedis open() {
    return jpool.getResource();
  }


  @Override
  protected void exit() {
    jpool.destroy();
    jpool = null;
  }


  /**
   * 计算持久化时使用的 ID
   */
  String genid(ISleepwalker sw, String type) {
    return "/" + type + "/" + sw.getClass().getName() + "/" + sw.getid();
  }


  String genid(Class clazz, String id, String type) {
    return "/" + type + "/" + clazz.getName() + "/" + id;
  }


  @Override
  public void sleep(ISleepwalker data) {
    if (data instanceof IBinData) {
      bin.sleep(genid(data, "BIN"), data);
    }
    else {
      json.sleep(genid(data, "JSON"), data);
    }
  }


  @Override
  public ISleepwalker wake(Class<? extends ISleepwalker> c, String id) {
    if (IBinData.class.isAssignableFrom(c)) {
      return bin.wake(c, genid(c, id, "BIN"));
    }
    else {
      return json.wake(c, genid(c, id, "JSON"));
    }
  }


  class JSON {
    public void sleep(String id, Object data) {
      try (Jedis client = jpool.getResource()) {
        String str = Tool.getAdapter((Class) data.getClass()).toJson(data);
        client.hset(KEY, id, str);
      }
    }

    public ISleepwalker wake(Class c, String id) {
      try (Jedis client = jpool.getResource()) {
        String str = client.hget(KEY, id);
        if (str == null) {
          throw new Exception("cannot found data " + c + " - " + id);
        }
        return (ISleepwalker) Tool.getAdapter(c).fromJson(str);
      } catch(Exception e) {
        log.error("wake json", e);
        return null;
      }
    }
  }


  class BIN {
    byte [] KEY_BYTE = KEY.getBytes();

    public void sleep(String id, Object data) {
      try (Jedis client = jpool.getResource()) {
        ByteArrayOutputStream obyte = new ByteArrayOutputStream();
        ObjectOutputStream oobj = new ObjectOutputStream(obyte);
        oobj.writeObject(data);
        oobj.flush();

        byte[] out = obyte.toByteArray();
//        Test.printArr(out);
        client.hset(KEY_BYTE, id.getBytes(), out);
      } catch(Exception e) {
        log.error("sleep bin", e);
      }
    }

    public IBinData wake(Class c, String id) {
      try (Jedis client = jpool.getResource()) {
        byte[] data = client.hget(KEY_BYTE, id.getBytes());
        if (data == null) {
          throw new Exception("cannot found data " + c + " - " + id);
        }
//        Test.printArr(data);
        ByteArrayInputStream ibyte = new ByteArrayInputStream(data);
        ObjectInputStream iobj = new ObjectInputStream(ibyte);

        return (IBinData) iobj.readObject();
      } catch(Exception e) {
        log.error("wake bin", e);
        return null;
      }
    }
  }
}
