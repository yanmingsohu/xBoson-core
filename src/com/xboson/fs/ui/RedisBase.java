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
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xboson.fs.ui.IUIFileProvider.ROOT;


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

  /** 搜索文件名称时最多返回数量, 超过的被忽略 */
  public static final int MAX_NAME_FILE = IUIFileProvider.MAX_RESULT_COUNT;

  public static final char PREFIX_FILE  = '|';
  public static final char PREFIX_DIR   = '?';
  public static final char PREFIX_DEL   = '>';
  public static final char PREFIX_MOVE  = '!';

  public static final String QUEUE_NAME    = "XB.UI.File.ChangeQueue";
  public static final String STRUCT_NAME   = "XB.UI.File.Struct";
  public static final String CONTENT_NAME  = "XB.UI.File.CONTENT";

  public static final byte[] STRUCT_NAMEB  = STRUCT_NAME.getBytes(CHARSET);
  public static final byte[] CONTENT_NAMEB = CONTENT_NAME.getBytes(CHARSET);

  private FindContentInRedisWithLua content_finder;
  private ThreadLocal<JedisSession> jedis_session;
  private Log log;


  public RedisBase() {
    this.log = LogFactory.create();
    this.jedis_session = new ThreadLocal<>();
    this.content_finder = new FindContentInRedisWithLua();
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
      if (! XBosonException.isChecked(e)) {
        log.warn("Read from redis but fail", e);
      }
      try (JedisSession js = openSession()) {
        js.client.hdel(STRUCT_NAME, path);
      }
      return null;

    } catch (IOException | ClassNotFoundException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 模糊查询符合路径的完整路径集合, 总是大小写敏感的
   */
  public FinderResult findPath(String pathName) {
    List<String> files = new ArrayList<>();
    int size = 0;

    try (JedisSession js = openSession()) {
      String cursor = RedisMesmerizer.BEGIN_OVER_CURSOR;
      ScanParams sp = new ScanParams();
      sp.match("*" + pathName + "*");

      for (;;) {
        ScanResult<Map.Entry<String, String>> sr =
              js.client.hscan(STRUCT_NAME, cursor, sp);

        for (Map.Entry<String, String> d : sr.getResult()) {
          files.add(d.getKey());
          if (++size >= MAX_NAME_FILE)
            break;
        }

        if (size >= MAX_NAME_FILE)
          break;

        cursor = sr.getStringCursor();
        if (cursor.equals(RedisMesmerizer.BEGIN_OVER_CURSOR))
          break;
      }
    }

    return new FinderResult(files, ROOT, pathName,
            true, size >= MAX_NAME_FILE);
  }


  /**
   * 查询文件内容, 返回文件列表, 该方法针对 redis 进行了优化
   *
   * @param basePath 开始目录
   * @param content 要搜索的文本
   * @param cs true 则启用大小写敏感
   */
  public FinderResult findContent(String basePath, String content, boolean cs) {
    return content_finder.find(basePath, content, cs);
  }


  /**
   * 向队列发送文件修改通知
   */
  public void sendModifyNotice(String path) {
    try (JedisSession js = openSession()) {
      js.client.rpush(QUEUE_NAME, PREFIX_FILE + path);
      clearContentFinderCache();
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


  public void sendMoveNotice(String from, String to) {
    try (JedisSession js = openSession()) {
      js.client.rpush(QUEUE_NAME, PREFIX_MOVE + from +':'+ to);
    }
  }


  /**
   * 打开一个文件修改监听器, 并尝试启动一个文件修改事件队列.
   */
  public FileModifyHandle startModifyReciver(IFileChangeListener fm) {
    UIEventMigrationThread.start();
    return new FileModifyHandle(fm);
  }


  public void clearContentFinderCache() {
    content_finder.clearCache();
  }


  /**
   * 打开一个 redis 事务, 或返回已经打开的事务, 支持嵌套打开.
   * 关闭 JedisSession 对象, 不要关闭其中的 client.
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
    /** 不要直接关闭该对象, 而是关闭 JedisSession */
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
