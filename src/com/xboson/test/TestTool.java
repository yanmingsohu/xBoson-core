////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午4:23:19
// 原始文件路径: xBoson/src/com/xboson/test/TestTool.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.been.XBosonException;
import com.xboson.fs.watcher.INotify;
import com.xboson.fs.watcher.IWatcher;
import com.xboson.fs.watcher.LocalDirWatcher;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TestTool extends Test {

  public void test() throws Throwable {
    tool();
    local_file_watcher();
    // uri_object();
    red(new XBosonException("test").getMessage());
    check_string_hash();
  }


  /**
   * Random len:  5 total: 1000000 conflict: 184  Used Time 2169 ms
   * Random len: 10 total: 1000000 conflict: 110  Used Time 2169 ms
   * Random len: 20 total: 1000000 conflict:  93  Used Time 2386 ms
   */
  public void check_string_hash() {
    sub("Check random string 10 length hash value conflict");
    int total = 1000000;
    int d = total / 10;
    int conflict = 0;
    int strlen = 5;

    beginTime();
    Map<Integer, String> check = new HashMap<>();
    for (int i=0; i<total; ++i) {
      String s = randomString(strlen);
      int hash = s.hashCode();
      String c = check.get(hash);

      if (c == null) {
        check.put(hash, s);
      } else if (!s.equals(c)){
        msg("hash conflict:", s, c, hash);
        ++conflict;
      }

      if (i % d == 0) {
        endTime("hash", i, "conflict:", conflict);
      }
    }
    endTime("Random len:", strlen, "total:", total, "conflict:", conflict);
  }


  /**
   * URI 是轻量对象, 可以大量使用
   *
   * create URI object 40000  Used Time 171 ms
   * ##### Heap utilization statistics [MB] #####
   *    Used Memory:35
   *    Free Memory:209
   *    Total Memory:245
   *    Max Memory:3620
   */
  public void uri_object() throws URISyntaxException {
    sub("Url parse speed");
    final int c= 50000;
    final int p = c / 5;

    URI[] arr = new URI[c];
    beginTime();

    for (int i=0; i<c; ++i) {
      arr[i] = new URI("test://", "localhost", "/TestTool");
      if (i % p == 0) {
        endTime("create URI object", i);
        memuse();
      }
    }
  }


	public void tool() throws Throwable {
		Exception e = create(20);
//		msg(Tool.allStack(e));
		msg(Tool.miniStack(e, 5));

		Set<Class> all = Tool.findPackage("com.xboson.test");
		msg("Tool.findPackage: " + all);
	}


	public void local_file_watcher() throws Throwable {
    LocalDirWatcher lfw = LocalDirWatcher.me();
    String base = SysConfig.me().readConfig().configPath;
    Path p = Paths.get(base);

    final boolean[] sw = new boolean[1];

    IWatcher w = lfw.watchAll(p, new INotify() {
      public void nofify(String basename, String filename, WatchEvent event,
                         WatchEvent.Kind kind) throws IOException {
        msg(kind, basename, filename, event.count());
        sw[0] = true;
      }
      public void remove(String basename) {
        msg("removed", basename);
      }
    });


    msg("Wait for DIR change:", p);
    Tool.sleep(1 * 1000);

    File testfile = new File(base + "/test_file.txt");
    FileWriter fw = new FileWriter(testfile);
    fw.write("Test file watcher\n");
    fw.write(lfw.getClass().toString());
    fw.write('\n');
    String str = new Date().toString();
    fw.write(str);
    fw.close();

    Tool.sleep(1 * 1000);
    ok(sw[0], "Received a file change notification");
  }
	
	
	public Exception create(int i) {
		if (i > 0) {
			return create(--i);
		}
		return new Exception("Test Stack");
	}


	public static void main(String[] a) {
		new TestTool();
	}
}
