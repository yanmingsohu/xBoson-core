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
// 文件创建日期: 18-8-13 下午5:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Pipe.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.event.EventLoop;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 很多 api 只接受一个输入流, 程序将数据全部写入缓冲区, 再从缓冲区读取,
 * 这种方法将不必要的占用内存. 该类将启动一个线程并限定一个上下文, 在上下文中将数据写出,
 * 数据做一个小的缓冲之后立即被读取, 写入和读取自动同步, 这将不会产生大量的内存占用.
 */
public class Pipe {

  /**
   * 和 EventLoop 混用容易死锁
   */
  private static final ExecutorService worker
          = Executors.newFixedThreadPool(10);

  private PipedOutputStream outs;
  private PipedInputStream ins;
  private Context context;


  public Pipe(Context c) throws IOException {
    if (c == null)
      throw new XBosonException.NullParamException("Context c");

    this.context = c;
    this.ins     = new PipedInputStream();
    this.outs    = new PipedOutputStream(ins);
  }


  /**
   * 该方法将打开输入流, 输入的数据来自 Context.run() 中的输出,
   * 必须尽可能从 InputStream 读取数据, 不要有另外的锁操作, 否则容易死锁.
   */
  public InputStream openInputStream() {
    worker.execute(() -> {
      try {
        context.run(outs);
      } finally {
        Tool.close(outs);
      }
    });
    return ins;
  }


  /**
   * 写出数据的限定范围
   */
  public interface Context {

    /**
     * 在该方法中写出数据, 实现无需关心线程同步等问题;
     * 该方法将在单独的线程中执行, 方法退出后 out 将被关闭.
     *
     * @param out 写出的数据将被输入流读取, 如果缓冲区已满, Write()方法将被临时阻塞
     */
    void run(OutputStream out);
  }
}
