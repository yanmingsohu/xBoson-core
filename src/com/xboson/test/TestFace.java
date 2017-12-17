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

    testLocal();
    test_redis_base();
    local_and_redis();
    test_sync_files();
    read_dir();
  }


  public void read_dir() {
    sub("Read dirs");

    String p = "/t/paas";
    Set<FileStruct> ls = local.readDir(p);
    Set<FileStruct> rs = redis.readDir(p);

    msg("Local:", ls);
    msg("Redis:", rs);
    eq(ls, rs, "same dir");
  }


  public void test_sync_files() {
    sub("SynchronizeFiles");
    SynchronizeFiles.start("/down1/web4ds/public");
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

    redis.deleteFile(test_file);
    redis.deleteFile(test_dir);
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
    public void noticeModifyContent(String file) {
    }
    @Override
    public void noticeMakeDir(String dirname) {
    }
    @Override
    public void noticeDelete(String file) {
    }
  }


  public static void main(String[] a) {
    new TestFace();
  }

}
