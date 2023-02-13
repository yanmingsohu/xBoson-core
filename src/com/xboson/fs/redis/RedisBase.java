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
// 文件创建日期: 17-11-19 上午11:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/RedisBase.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Hex;
import com.xboson.util.c0nst.IConstant;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xboson.fs.redis.IRedisFileSystemProvider.ROOT;


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
  public static final int MAX_NAME_FILE = IRedisFileSystemProvider.MAX_RESULT_COUNT;

  public static final char PREFIX_FILE  = '|';
  public static final char PREFIX_DIR   = '?';
  public static final char PREFIX_DEL   = '>';
  public static final char PREFIX_MOVE  = '!';

  public final String queueName;
  public final String structName;
  public final String contentName;

  public final byte[] structNameBytes;
  public final byte[] contentNameBytes;

  private IFileSystemConfig config;
  private FindContentInRedisWithLua content_finder;
  private ThreadLocal<JedisSession> jedis_session;
  private Log log;


  public RedisBase(IFileSystemConfig config) {
    this.log              = LogFactory.create();
    this.jedis_session    = new ThreadLocal<>();
    this.queueName        = config.configQueueName();
    this.structName       = config.configStructName();
    this.contentName      = config.configContentName();
    this.structNameBytes  = this.structName.getBytes(CHARSET);
    this.contentNameBytes = this.contentName.getBytes(CHARSET);
    this.config           = config;
    this.content_finder   = new FindContentInRedisWithLua(config);
  }


  /**
   * 将 fs 中的内容保存
   */
  public void setContent(RedisFileAttr fs) {
    try (JedisSession js = openSession()) {
      js.client.hset(contentNameBytes,
              fs.path.getBytes(CHARSET), fs.getFileContent());
    }
  }


  /**
   * 读取文件内容到 fs
   */
  public void getContent(RedisFileAttr fs) {
    if (!fs.isFile())
      throw new XBosonException("is not file");

    try (JedisSession js = openSession()) {
      byte[] b = js.client.hget(contentNameBytes, fs.path.getBytes(CHARSET));
      fs.setFileContent(b);
    }
  }


  public void delContent(RedisFileAttr fs) {
    if (!fs.isFile())
      throw new XBosonException("is not file");

    try (JedisSession js = openSession()) {
      js.client.hdel(contentName, fs.path);
    }
  }

  /**
   * 创建/保存一个独立的节点, 不管有没有父节点的存在,
   * 文件内容需要单独通过 setContent() 操作
   */
  public void saveStruct(RedisFileAttr struct) {
    try (JedisSession js = openSession()) {
      byte[] out = Hex.toBytes(struct);
      js.client.hset(structNameBytes, struct.path.getBytes(CHARSET), out);

    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }


  public void removeStruct(String path) {
    try (JedisSession js = openSession()) {
      js.client.hdel(structName, path);
    }
  }


  public void removeStruct(RedisFileAttr fs) {
    removeStruct(fs.path);
  }


  /**
   * 路径上没有数据返回 null
   */
  public RedisFileAttr getStruct(String path) {
    if (path == null)
      throw new XBosonException.NullParamException("String path");

    try (JedisSession js = openSession()) {
      byte[] bin = js.client.hget(structNameBytes, path.getBytes(CHARSET));
      if (bin == null || bin.length == 0)
        return null;

      return (RedisFileAttr) Hex.fromBytes(bin);

    } catch (ObjectStreamException e) {
      if (! XBosonException.isChecked(e)) {
        log.warn("Read from redis but fail", e);
      }
      try (JedisSession js = openSession()) {
        js.client.hdel(structName, path);
      }
      return null;

    } catch (IOException | ClassNotFoundException e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 查询符合路径的完整路径集合, 总是大小写敏感的, 可以使用匹配模式.
   * 默认只返回完全符合 pathName 的路径名.
   */
  public FinderResult findPath(String pathName) {
    List<String> files = new ArrayList<>();
    int size = 0;

    try (JedisSession js = openSession()) {
      String cursor = RedisMesmerizer.BEGIN_OVER_CURSOR;
      ScanParams sp = new ScanParams();
      sp.match(pathName);

      for (;;) {
        ScanResult<Map.Entry<String, String>> sr =
              js.client.hscan(structName, cursor, sp);

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
      js.client.rpush(queueName, PREFIX_FILE + path);
      clearContentFinderCache();
    }
  }


  public void sendCreateDirNotice(String dir) {
    try (JedisSession js = openSession()) {
      js.client.rpush(queueName, PREFIX_DIR + dir);
    }
  }


  public void sendDeleteNotice(String dir) {
    try (JedisSession js = openSession()) {
      js.client.rpush(queueName, PREFIX_DEL + dir);
    }
  }


  public void sendMoveNotice(String from, String to) {
    try (JedisSession js = openSession()) {
      js.client.rpush(queueName, PREFIX_MOVE + from +':'+ to);
    }
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


  public IFileSystemConfig getConfig() {
    return config;
  }


  /**
   * 维护线程上打开唯一的 Jedis 连接, 使连接跨越函数调用.
   */
  public class JedisSession implements AutoCloseable {
    /** 不要直接关闭该对象, 而是关闭 JedisSession */
    public final IRedis client;
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
