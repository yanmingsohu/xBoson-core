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

import com.xboson.fs.watcher.INotify;
import com.xboson.fs.watcher.IWatcher;
import com.xboson.fs.watcher.LocalDirWatcher;
import com.xboson.util.Tool;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Set;


public class TestTool extends Test {

  public void test() throws Throwable {
    tool();
    local_file_watcher();
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
    Path p = Paths.get(
            "C:\\Users\\jym\\xBoson-config\\");

    final Thread curr = Thread.currentThread();


    IWatcher w = lfw.watchAll(p, new INotify() {
      public void nofify(String basename, String filename, WatchEvent event,
                         WatchEvent.Kind kind) throws IOException {
        msg(kind, basename, filename, event.count());
        curr.interrupt();
      }

      public void remove(String basename) {
        msg("removed", basename);
      }
    });


    red("Wait for DIR change:", p);
    Tool.sleep(10 * 1000);
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
