////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-18 下午8:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/ui/LocalFileMapping.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.ui;

import com.xboson.been.Config;
import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.SysConfig;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;


/**
 * 本地文件模式, 集群中除了一个本地模式, 其他节点都是 redis 模式;
 * 本地文件模式负责 redis 与本地文件的同步
 *
 * 启动: 将最新的文件同步到 redis, 读取 redis 上的修改通知队列同步到本地.
 * 读取: 比较本地文件和 redis 文件修改日期, 相同返回 redis, 否则做同步后返回最新文件.
 * 写入: 更新本地文件后更新 redis 文件, 不发送任何通知.
 */
public class LocalFileMapping implements IUIFileProvider, IFileModify {

  private RedisBase rb;
  private String basepath;
  private Log log;


  public LocalFileMapping() {
    Config cf = SysConfig.me().readConfig();
    this.basepath = cf.uiUrl;
    this.log = LogFactory.create();
    this.rb = new RedisBase();
    rb.startModifyReciver(this);
    SynchronizeFiles.start();
  }


  /**
   * 保证返回的文件一定在 basepath 的子目录中.
   * 返回文件的本地目录
   */
  public Path normalize(String path) {
    return Paths.get(basepath, path);
  }


  @Override
  public byte[] readFile(String path) throws IOException {
    Path local_file = normalize(path);
    long local = Files.getLastModifiedTime(local_file).toMillis();
    long redis = rb.getModifyTime(path);
    byte[] ret;

    if (local == redis) {
      ret = rb.readFile(path);
    }
    if (local > redis) {
      if (Files.isDirectory(local_file)) {
        throw new XBosonException.ISDirectory(local_file);
      }
      ret = Files.readAllBytes(local_file);
      rb.writeFile(path, ret);
      rb.writeModifyTime(path, local);
    }
    else /* local < redis */ {
      ret = rb.readFile(path);
      Files.write(local_file, ret);
      Files.setLastModifiedTime(local_file, FileTime.fromMillis(redis));
    }
    return ret;
  }


  @Override
  public long modifyTime(String path) {
    try {
      Path local_file = normalize(path);
      long local = Files.getLastModifiedTime(local_file).toMillis();
      long redis = rb.getModifyTime(path);
      return local >= redis ? local : redis;
    } catch(IOException e) {
      return -1;
    }
  }


  @Override
  public void makeDir(String path) {
    try {
      Path file = normalize(path);
      Files.createDirectories(file);
    } catch (Exception e) {
      log.error("Make dir", e);
    }
  }


  @Override
  public void writeFile(String path, byte[] bytes) throws IOException {
    Path local_file = normalize(path);
    long modified_time = System.currentTimeMillis();
    Files.write(local_file, bytes);
    Files.setLastModifiedTime(local_file, FileTime.fromMillis(modified_time));
    rb.writeFile(path, bytes);
    rb.writeModifyTime(path, modified_time);
  }


  /**
   * 来自 redis 的文件变通通知, 用远程文件覆盖本地文件
   * @param file 改动的文件路径
   */
  @Override
  public void modify(String file) {
    try {
      long mt = rb.getModifyTime(file);
      byte[] content = rb.readFile(file);
      Path local_file = normalize(file);
      Files.write(local_file, content);
      Files.setLastModifiedTime(local_file, FileTime.fromMillis(mt));
    } catch(Exception e) {
      log.error("Received modification:", e);
    }
  }


}
