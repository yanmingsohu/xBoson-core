////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-20 上午9:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/SynchronizeFiles.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.ui;

import com.xboson.event.timer.EarlyMorning;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;


/**
 * 同步 redis 和本地文件系统中的所有文件
 */
public final class SynchronizeFiles implements Runnable, FileVisitor<Path> {

  private static Thread t = null;


  /**
   * 启动同步线程, 如果线程已经启动则立即返回,
   * 同一个时刻只能启动一个同步线程.
   * @param path 本地文件路径
   */
  public synchronized static void start(String path) {
    if (t != null) return;
    SynchronizeFiles sf = new SynchronizeFiles(path);
    t = new Thread(sf);
    t.setDaemon(true);
    t.setPriority(Thread.MIN_PRIORITY);
    t.start();
  }


  /**
   * 注册到凌晨事件中, 每天凌晨触发同步
   * @param path 本地文件路径
   */
  public static void regEarlyMorningClear(final String path) {
    SynchronizeFiles sf = new SynchronizeFiles(path);
    EarlyMorning.add(sf);
  }


  /**
   * 等待线程结束后返回, 如果线程已经停止或没有启动则立即返回.
   */
  public synchronized static void join() {
    Tool.waitOver(t);
  }


  private RedisBase rb;
  private Log log;
  private Path base;
  private long begin_time;
  private int files = 0;
  private int dirs  = 0;
  private int d     = 50;


  private SynchronizeFiles(String base) {
    this.base = Paths.get(base);
    this.log  = LogFactory.create();
    this.rb   = new RedisBase();
  }


  @Override
  public void run() {
    log.info("Start Synchronize ui files", base);
    begin_time = System.currentTimeMillis();
    try {
      Files.walkFileTree(base, this);
    } catch (IOException e) {
      log.error(e);
    }
    log.info("Sync Over,", files, "files and", dirs,
            "directorys, use", System.currentTimeMillis()-begin_time, "ms");
    t = null;
  }


  @Override
  public FileVisitResult preVisitDirectory(
          Path path, BasicFileAttributes basicFileAttributes)
          throws IOException {
    ++dirs;
    return FileVisitResult.CONTINUE;
  }


  @Override
  public FileVisitResult visitFile(
          Path local_path, BasicFileAttributes basicFileAttributes)
          throws IOException {

    //
    // 转换为 redis 上的虚拟路径
    //
    String vpath = Tool.normalize("/"+ base.relativize(local_path));

    long local = Files.getLastModifiedTime(local_path).toMillis();
    long redis = rb.getModifyTime(vpath);

    if (local > redis) {
      byte[] body = Files.readAllBytes(local_path);
      rb.writeFile(vpath, body);
      rb.writeModifyTime(vpath, local);
    }
    else if (local < redis) {
      byte[] body = rb.readFile(vpath);
      Files.write(local_path, body);
      Files.setLastModifiedTime(local_path, FileTime.fromMillis(redis));
    }

    if (++files > d) {
      d = files * 2;
      log.debug("Process", files, ", use",
              System.currentTimeMillis()-begin_time, "ms", "...");
    }
    return FileVisitResult.CONTINUE;
  }


  @Override
  public FileVisitResult visitFileFailed(
          Path path, IOException e) throws IOException {
    if (e != null) {
      log.error(e);
    }
    return FileVisitResult.CONTINUE;
  }


  @Override
  public FileVisitResult postVisitDirectory(
          Path path, IOException e) throws IOException {
    if (e != null) {
      log.error(e);
    }
    return FileVisitResult.CONTINUE;
  }
}
