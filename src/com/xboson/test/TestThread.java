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
// 文件创建日期: 18-1-27 上午8:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestThread.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.Ref;
import com.xboson.util.ThreadMonitor;
import com.xboson.util.Tool;


public class TestThread extends Test {


  public static void main(String[] a) {
    new TestThread();
  }


  @Override
  public void test() throws Throwable {
    test_stop();
    monitor();
  }


  private void test_stop() throws InterruptedException {
    sub("Thread stop()");
    Thread dead = new Thread(()->{
      boolean t = true;
      try {
        while (t) {
          // 死循环
        }
      } catch (ThreadDeath e) {
        success("dead", e);
      } finally {
        msg("dead finally");
      }
      msg("dead exit");
    });

    dead.setName("Dead loop threads, needed stop()");
    msg("start");
    dead.start();
    Tool.sleep(1000);
    msg("interrupt");
    dead.interrupt();
    Tool.sleep(1000);
    msg("stop");
    dead.stop();
    msg("main exit");
    dead.join();
  }


  private void monitor() throws InterruptedException {
    sub("Thread monitor");
    final ThreadMonitor m = new ThreadMonitor();
    final Ref<Integer> stop_flag = new Ref<Integer>(0);

    m.setStopMethod(new ThreadMonitor.IStopper() {
      @Override
      public void stop(Thread thread) {
        thread.stop();
        stop_flag.x++;
        msg(thread.getName(), "stop");
      }
    });

    Thread t1 = new Thread("Thread-1") {
      public void run() {
        try {
          Thread.sleep(100);
          success("Thread 1 run end");
        } catch (ThreadDeath td) {
          fail("Should not terminate the thread 1", td);
        } catch (InterruptedException e) {
          fail("Thread 1", e);
        } finally {
          m.purge();
          msg("Thread 1 exit");
        }
      }
    };
    t1.start();
    m.look(t1, 1000);
    t1.join();

    Thread t2 = new Thread("Thread-2") {
      public void run() {
        try {
          Thread.sleep(1000);
          fail("Should terminate the thread 2");
        } catch(ThreadDeath td) {
          success("Thread 2 Received a termination signal");
        } catch (InterruptedException e) {
          fail("Thread 2", e);
        } finally {
          m.purge();
          msg("Thread 2 exit");
        }
      }
    };
    t2.start();
    m.look(t2, 100);
    t2.join();

    eq(stop_flag.x, 1, "Defined stop method");
  }

}
