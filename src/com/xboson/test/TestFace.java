////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-19 上午10:40
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestFace.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.XBosonException;
import com.xboson.fs.ui.*;
import com.xboson.log.Level;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;


/**
 * UI-FS 测试
 * 如果有其他的集群节点连接了 redis, 测试会失败
 */
public class TestFace extends Test {

  private String path;
  private byte[] content;

  private LocalFileMapping local;
  private RedisFileMapping redis;


  public void test() throws Throwable {
    sub("Test ui file system");

    local = new LocalFileMapping();
    redis = new RedisFileMapping();
    path  = "/ui/paas/login.html";
    LogFactory.setLevel(Level.ALL);

    test_sync_files();
    testLocal();
    test_redis_base();
    local_and_redis();
    read_dir();
    test_move();
  }


  public void test_move() {
    sub("Test move");
    final byte[] s1 = randomString(100).getBytes(CHARSET);
    final byte[] s2 = randomString(200).getBytes(CHARSET);

    del("/t2/s1.txt", "/t2/t3/t4/s2.txt", "/t2/t3/t4",
            "/t2/t3", "/t2");

    del("/m4/s1.txt", "/m4/t3/t4/s2.txt",
            "/m4/t3/t4", "/m4/t3", "/m4");

    redis.makeDir("/t2");
    redis.makeDir("/t2/t3/t4");
    redis.writeFile("/t2/s1.txt", s1);
    redis.writeFile("/t2/t3/t4/s2.txt", s2);

    redis.move("/t2", "/m4");

    //
    // 写入的文件不是立即就可以读取, 消息传递有延迟
    //
    Tool.sleep(1000);

    file_eq(s1, "/m4/s1.txt");
    file_eq(s2, "/m4/t3/t4/s2.txt");
    dir_eq("/m4");
    dir_eq("/m4/t3");
    dir_eq("/m4/t3/t4");

    notExists("/t2/s1.txt", "/t2/t3/t4/s2.txt", "/t2/t3/t4",
            "/t2/t3", "/t2");

    del("/m4/s1.txt", "/m4/t3/t4/s2.txt",
            "/m4/t3/t4", "/m4/t3", "/m4");
  }


  /**
   * 从两个系统中读取文件, 必须与 content 相同
   */
  public void file_eq(byte[] content, String fileName) {
    byte[] s1 = redis.readFile(fileName);
    byte[] s2 = local.readFile(fileName);
    ok(Arrays.equals(s1, content), "redis file ok");
    ok(Arrays.equals(s2, content), "local file ok");
  }


  public void notExists(String ...files) {
    for (String file : files) {
      FileStruct fs1 = redis.readAttribute(file);
      FileStruct fs2 = local.readAttribute(file);
      ok(fs1 == null, "Redis no file:" + file);
      ok(fs2 == null, "Local no file:" + file);
    }
  }


  /**
   * 删除若干文件, 无视错误
   */
  public void del(String ...files) {
    for (String name : files) {
      try {
        redis.delete(name);
      } catch (Exception e) {
        msg("DEBUG: Delete", name, e);
      }
    }
  }


  /**
   * 从两个系统中读取目录, 目录内容相同测试正确, 否则抛出异常
   */
  public void dir_eq(String dir) {
    Set<FileStruct> ls = local.readDir(dir);
    Set<FileStruct> rs = redis.readDir(dir);
    try {
      ok(ls != null, "local dir");
      ok(rs != null, "redis dir");
      eq(ls, rs, "same dir");
      msg("OK same dir", dir);
    } catch (AssertionError t) {
      msg("Local:", ls);
      msg("Redis:", rs);
      throw t;
    }
  }


  public void read_dir() {
    sub("Read dirs");
    dir_eq("/t/paas");
  }


  public void test_sync_files() {
    sub("SynchronizeFiles");
    SynchronizeFiles.join();
  }


  public void local_and_redis() throws Throwable {
    sub("Write use redis, read from local");

    String test_dir = "/test/";
    String test_file = test_dir + "/a.js";
    byte[] content = randomString(100).getBytes();
    redis.makeDir("/test/");
    redis.writeFile(test_file, content);
    Tool.sleep(1000);

    byte[] r = local.readFile(test_file);
    ok(Arrays.equals(content, r), "content");

    Path p = local.normalize(test_file);
    byte[] r2 = Files.readAllBytes(p);
    ok(Arrays.equals(content, r2), "from fs");
    msg("content:", new String(r2));

    delete_non_empty_dir(test_dir);

    redis.delete(test_file);
    redis.delete(test_dir);
  }


  /**
   * dir 是非空目录, 删除会抛出异常, 则完成测试, 若没有异常则是系统错误
   */
  public void delete_non_empty_dir(String dir) {
    sub("Check delte non-empty DIR", dir);
    boolean checkNon = false;
    try {
      redis.delete(dir);
    } catch (XBosonException.IOError io) {
      checkNon = io.toString().indexOf("non-empty dir") >= 0;
    }
    if (checkNon) {
      msg("OK, check non-empty dir");
    } else {
      fail("Delete non-empty dir");
    }
    ok(null != redis.readDir(dir), "redis not delete");
    ok(null != local.readDir(dir), "local not delete");
  }


  public void testLocal() throws Throwable {
    sub("Local file system");

    content = local.readFile(path);
    String s = new String(content);
    ok(s.indexOf("云平台登录")>=0, "html file read");
    ok(s.indexOf("忘记密码")>=0, "check ok");
  }


  public void test_redis_base() throws Throwable {
    sub("Test UI Redis base");

    RedisBase rb = new RedisBase();
    FileStruct fs = FileStruct.createFile(path, 0, content);
    rb.setContent(fs);
    rb.getContent(fs);
    ok(Arrays.equals(fs.getFileContent(), content), "read/write redis");

    final Thread curr = Thread.currentThread();
    final boolean[] check = new boolean[1];

    FileModifyHandle fmh = rb.startModifyReciver(new NullModifyListener() {
      public void noticeModifyContent(String file) {
        eq(path, file, "recive modify notice");
        check[0] = true;
        curr.interrupt(); // 中断 标记1 的休眠.
      }
    });

    rb.sendModifyNotice(path);
    Tool.sleep(10000); // 标记1
    ok(check[0], "waiting message, (如果运行了其他节点这个测试会失败.)");
    fmh.removeModifyListener();
  }


  abstract class NullModifyListener implements IFileChangeListener {
    @Override
    public void noticeModifyContent(String file) {}
    @Override
    public void noticeMakeDir(String dirname) {}
    @Override
    public void noticeDelete(String file) {}
    @Override
    public void noticeMove(String form, String to) {}
  }


  public static void main(String[] a) {
    new TestFace();
  }

}
