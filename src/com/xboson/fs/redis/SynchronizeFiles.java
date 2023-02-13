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
// 文件创建日期: 17-11-20 上午9:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/ui/SynchronizeFiles.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.event.EventLoop;
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

  private RedisFileMapping rfm;
  private RedisBase rb;
  private Log log;
  private Path base;
  private long begin_time;
  private int files = 0;
  private int dirs  = 0;
  private int d     = 50;


  /**
   * 创建一个文件同步器, 同步本地文件到 redis;
   * 文件同步器创建后不会启动, 需要放入其他的任务管理器中
   *
   * @param rb 基础方法
   * @param rfm 扩展方法
   * @see EventLoop 全局单线程任务管理
   * @see EarlyMorning 午夜任务管理器
   */
  public SynchronizeFiles(RedisBase rb, RedisFileMapping rfm) {
    this.log  = LogFactory.create();
    this.rb   = rb;
    this.rfm  = rfm;
    this.base = Paths.get(rb.getConfig().configLocalPath());
  }


  @Override
  public void run() {
    try (RedisBase.JedisSession close = rb.openSession()) {

      log.info("Start At:", base);
      begin_time = System.currentTimeMillis();

      log.info("Local TO Redis");
      Files.walkFileTree(base, this);

      //
      // Redis to Local
      // 这个机能借助于文件修改消息队列来实现, 程序启动后将收到 redis 修改历史
      // 将历史合并到本机目录中.
      //
    } catch (IOException e) {
      log.error(e);
    } finally {
      long used = System.currentTimeMillis() - begin_time;
      log.info("Sync Over,", files,
               "files and", dirs, "directorys, use", used, "ms");
    }
  }


  /**
   * 转换为 redis 上的虚拟路径
   * @param local
   */
  private String getVirtualPath(Path local) {
    return Tool.normalize("/"+ base.relativize(local));
  }


  @Override
  public FileVisitResult preVisitDirectory(
          Path local_path, BasicFileAttributes basicFileAttributes)
          throws IOException {
    String vpath = getVirtualPath(local_path);
    rfm.makeDir(vpath, false);
    ++dirs;

    return FileVisitResult.CONTINUE;
  }


  @Override
  public FileVisitResult visitFile(
          Path local_path, BasicFileAttributes basicFileAttributes)
          throws IOException {

    String vpath = getVirtualPath(local_path);

    final long local_t = Files.getLastModifiedTime(local_path).toMillis();

    RedisFileAttr redis_file = rb.getStruct(vpath);
    final long redis_t = redis_file != null ? redis_file.lastModify : -1;

    if (local_t > redis_t) {
      byte[] body = Files.readAllBytes(local_path);
      redis_file = RedisFileAttr.createFile(vpath, local_t, body);
      rfm.writeFile(redis_file, false);
    }
    else if (local_t < redis_t) {
      rb.getContent(redis_file);
      byte[] body = redis_file.getFileContent();
      Files.write(local_path, body);
      Files.setLastModifiedTime(local_path, FileTime.fromMillis(redis_t));
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
