////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-19 上午11:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/RedisBase.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.IConstant;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.ObjectStreamException;


/**
 * redis 的底层操作, 没有目录的概念;
 * 所有的参数都不做任何前置处理, 调用者需要保证参数正确;
 * 特别是路径参数必须规范化并统一使用 unix 风格路径字符串.
 *
 * 如果本地文件节点下线, 使用 GlobalEventBus 可能会丢失消息,
 * 所以这里单独使用队列来实现文件修改消息.
 *
 * 多个操作使用 "try (JedisSession js = openSession()) {}" 来操作.
 */
public class RedisBase implements IConstant {

  public static final char PREFIX_FILE  = '|';
  public static final char PREFIX_DIR   = '?';
  public static final char PREFIX_DEL   = '>';

  public static final String QUEUE_NAME    = "XB.UI.File.ChangeQueue";
  public static final String STRUCT_NAME   = "XB.UI.File.Struct";
  public static final String CONTENT_NAME  = "XB.UI.File.CONTENT";

  public static final byte[] STRUCT_NAMEB  = STRUCT_NAME.getBytes(CHARSET);
  public static final byte[] CONTENT_NAMEB = CONTENT_NAME.getBytes(CHARSET);

  private ThreadLocal<JedisSession> jedis_session;
  private Log log;


  public RedisBase() {
    this.log = LogFactory.create();
    this.jedis_session = new ThreadLocal<>();
  }


  /**
   * 将 fs 中的内容保存
   */
  public void setContent(FileStruct fs) {
    try (JedisSession js = openSession()) {
      js.client.hset(CONTENT_NAMEB,
              fs.path.getBytes(CHARSET), fs.getFileContent());
    }
  }


  /**
   * 读取文件内容到 fs
   */
  public void getContent(FileStruct fs) {
    if (!fs.isFile())
      throw new XBosonException("is not file");

    try (JedisSession js = openSession()) {
      byte[] b = js.client.hget(CONTENT_NAMEB, fs.path.getBytes(CHARSET));
      fs.setFileContent(b);
    }
  }


  public void delContent(FileStruct fs) {
    if (!fs.isFile())
      throw new XBosonException("is not file");

    try (JedisSession js = openSession()) {
      js.client.hdel(CONTENT_NAME, fs.path);
    }
  }

  /**
   * 创建/保存一个独立的节点, 不管有没有父节点的存在,
   * 文件内容需要单独通过 setContent() 操作
   */
  public void saveStruct(FileStruct struct) {
    try (JedisSession js = openSession()) {
      byte[] out = RedisMesmerizer.toBytes(struct);
      js.client.hset(STRUCT_NAMEB, struct.path.getBytes(CHARSET), out);

    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  public void removeStruct(String path) {
    try (JedisSession js = openSession()) {
      js.client.hdel(STRUCT_NAME, path);
    }
  }


  public void removeStruct(FileStruct fs) {
    removeStruct(fs.path);
  }


  /**
   * 路径上没有数据返回 null
   */
  public FileStruct getStruct(String path) {
    try (JedisSession js = openSession()) {
      byte[] bin = js.client.hget(STRUCT_NAMEB, path.getBytes(CHARSET));
      if (bin == null || bin.length == 0)
        return null;

      return (FileStruct) RedisMesmerizer.fromBytes(bin);

    } catch (ObjectStreamException e) {
      log.warn("Read from redis but fail", e);
      try (JedisSession js = openSession()) {
        js.client.hdel(STRUCT_NAME, path);
      }
      return null;

    } catch (IOException | ClassNotFoundException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 向队列发送文件修改通知
   */
  public void sendModifyNotice(String path) {
    try (JedisSession js = openSession()) {
      js.client.rpush(QUEUE_NAME, PREFIX_FILE + path);
    }
  }


  public void sendCreateDirNotice(String dir) {
    try (JedisSession js = openSession()) {
      js.client.rpush(QUEUE_NAME, PREFIX_DIR + dir);
    }
  }


  public void sendDeleteNotice(String dir) {
    try (JedisSession js = openSession()) {
      js.client.rpush(QUEUE_NAME, PREFIX_DEL + dir);
    }
  }


  /**
   * 打开一个文件修改监听器, 并尝试启动一个文件修改事件队列.
   */
  public FileModifyHandle startModifyReciver(IFileChangeListener fm) {
    UIEventMigrationThread.start();
    return new FileModifyHandle(fm);
  }


  /**
   * 打开一个 redis 事务, 或返回已经打开的事务, 支持嵌套打开
   */
  public JedisSession openSession() {
    JedisSession js = jedis_session.get();
    if (js == null) {
      js = new JedisSession();
      jedis_session.set(js);
    } else {
      ++js.nested;
    }
    return js;
  }


  /**
   * 维护线程上打开唯一的 Jedis 连接, 使连接跨越函数调用.
   */
  public class JedisSession implements AutoCloseable {
    public final Jedis client;
    private int nested;

    private JedisSession() {
      this.client = RedisMesmerizer.me().open();
      this.nested = 0;
    }

    @Override
    public void close() {
      if (nested > 0) {
        --nested;
      } else {
        client.close();
        jedis_session.remove();
      }
    }
  }
}
