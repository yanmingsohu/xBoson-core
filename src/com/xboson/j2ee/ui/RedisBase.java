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

package com.xboson.j2ee.ui;

import com.xboson.event.*;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.RedisMesmerizer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;


/**
 * redis 的底层操作,
 * 所有的参数都不做任何前置处理, 调用者需要保证参数正确.
 */
public class RedisBase {

  public static final char PREFIX_FILE  = '|';
  public static final char PREFIX_DIR   = '?';
  public static final char PREFIX_DEL   = '>';

  public static final String QUEUE_NAME   = "XB.UI.File.ChangeQueue";
  public static final String CONTENT_NAME = "XB.UI.File.Content";
  public static final String MODIFY_NAME  = "XB.UI.File.ModifyTime";

  public static final byte[] CONTENT_ARR  = CONTENT_NAME.getBytes();

  private Log log;


  public RedisBase() {
    this.log = LogFactory.create();
  }


  /**
   * 只是写入文件内容
   */
  public void writeFile(String path, byte[] bytes) throws IOException {
    try (Jedis client = RedisMesmerizer.me().open()) {
      client.hset(CONTENT_ARR, path.getBytes(), bytes);
    }
  }


  /**
   * 写入文件修改日期
   */
  public void writeModifyTime(String path, long file_modify_time) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      String time = Long.toHexString(file_modify_time);
      client.hset(MODIFY_NAME, path, time);
    }
  }


  /**
   * 读取文件
   */
  public byte[] readFile(String path) throws IOException {
    try (Jedis client = RedisMesmerizer.me().open()) {
      return client.hget(CONTENT_ARR, path.getBytes());
    }
  }


  public long getModifyTime(String path) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      String time = client.hget(MODIFY_NAME, path);
      if (time != null) {
        return Long.parseLong(time, 16);
      }
    }
    return -1;
  }


  public void deleteFile(String path) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      Transaction t = client.multi();
      client.hdel(CONTENT_ARR, path.getBytes());
      client.hdel(MODIFY_NAME, path);
      t.exec();
    }
  }


  /**
   * 向队列发送文件修改通知
   */
  public void sendModifyNotice(String path) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      client.rpush(QUEUE_NAME, PREFIX_FILE + path);
    }
  }


  public void sendCreateDirNotice(String dir) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      client.rpush(QUEUE_NAME, PREFIX_DIR + dir);
    }
  }


  public void sendDeleteNotice(String dir) {
    try (Jedis client = RedisMesmerizer.me().open()) {
      client.rpush(QUEUE_NAME, PREFIX_DEL + dir);
    }
  }


  /**
   * 打开一个文件修改监听器, 并尝试启动一个文件修改事件队列.
   */
  public FileModifyHandle startModifyReciver(IFileModify fm) {
    UIEventMigrationThread.start();
    return new FileModifyHandle(fm);
  }


}
