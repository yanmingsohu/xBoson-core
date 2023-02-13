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
// 文件创建日期: 2017年11月11日 09:05
// 原始文件路径: xBoson/src/com/xboson/test/TestEvent.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.squareup.moshi.JsonAdapter;
import com.xboson.event.GlobalEventBus;
import com.xboson.event.GlobalListener;
import com.xboson.event.Names;
import com.xboson.event.QuickSender;
import com.xboson.event.timer.EarlyMorning;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;

import javax.naming.Binding;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import java.io.IOException;
import java.util.Date;

public class TestEvent extends Test implements GlobalListener {

  private Object recv = null;
  private int count = 0;
  private boolean not_recive_when_off = true;


  public void test() throws Exception {
    GlobalEventBus ge = GlobalEventBus.me();
    ge.on("test", this);
    ge.on(Names.inner_error, this);
    TestFace.waitEventLoopEmpty();

    send(new Date().toString());
    send("hello.2=" + Math.random());
    send(new TestData());

    ok(recv != null, "recive data");
    eq(3, count, "recive count");

    QuickSender.emitError(new Exception("yes"), this);

    not_recive_when_off = true;
    ge.off("test", null);
    ge.emit("test", "not recive");
    ok(not_recive_when_off, "off() remove listener");

    ge.off(Names.inner_error,null);

    test_recv();
    timer();
  }


  public void timer() throws Exception {
    msg(EarlyMorning.class, EarlyMorning.first, EarlyMorning.hour24);
  }


  private static String send = "PUBLISH \"/com.xboson.event.GlobalEventBus/recv\" " +
          "\"{\\\"className\\\":\\\"com.xboson.test.Test$TestData\\\"," +
          "\\\"data\\\":\\\"{\\\\\\\"a\\\\\\\":957,\\\\\\\"b\\\\\\\":17165," +
          "\\\\\\\"name\\\\\\\":\\\\\\\"oEp0fi1XiKEA7w==\\\\\\\"}\\\"," +
          "\\\"from\\\":1,\\\"type\\\":3}\"";

  private static String quit = "PUBLISH \"/com.xboson.event.GlobalEventBus/recv\" " +
          "\"{\\\"data\\\":\\\"quit\\\",\\\"from\\\":1,\\\"type\\\":3}\"";


  void test_recv() throws InterruptedException {
    msg("命令用来模拟另一个节点发送数据:");
    sub(send);
    msg("命令可以让进程退出:");
    sub(quit);
    msg("等待另一个节点或 Redis 客户端发送来数据...");

    WaitRecv _wait = new WaitRecv();
    GlobalEventBus ge = GlobalEventBus.me();
    ge.on("recv", _wait);
    ge.on(Names.inner_error, _wait);

    new SendThread().start();
    Thread t = new Thread(_wait);
    t.start();
    t.join();
  }


  /**
   * 用 redis 客户端发送一条消息, 这里会接收到
   */
  class WaitRecv implements GlobalListener, Runnable {
    boolean over = false;

    @Override
    public void run() {
      while (!over) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void objectChanged(NamingEvent namingEvent) {
      String name = namingEvent.getNewBinding().getName();
      Object data = namingEvent.getNewBinding().getObject();

      msg("Recv in Thread "+ name + " " + data);
      if (data.equals("quit")) {
        over = true;
        msg("OK bye !!");
      }
    }

    @Override
    public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
      namingExceptionEvent.getException().printStackTrace();
    }
  }


  class SendThread extends Thread {
    public void run() {
      try (IRedis client = RedisMesmerizer.me().open()) {
        _send_cmd(send, client);
        sub("Wait to Send quit..");
        Tool.sleep(2000);
        _send_cmd(quit, client);
        sub("Send Quit.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    void _send_cmd(String cmd, IRedis client) throws IOException {
      String[] sp = cmd.split(" ", 3);
      JsonAdapter<String> ja = Tool.getAdapter(String.class);
      String ch = ja.fromJson(sp[1]);
      String data = ja.fromJson(sp[2]);

      client.publish(ch, data);
    }
  }


  void send(Object data) {
    GlobalEventBus ge = GlobalEventBus.me();
    ge.emit("test", data);
    Tool.sleep(1000);
    eq(recv, data, "recive: " + recv);
  }


  @Override
  public void objectChanged(NamingEvent e) {
    Binding b = e.getNewBinding();
    String name = b.getName();
    Object data = b.getObject();
    recv = data;

    msg(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> [get event]"
            + "\n\t\t\tname: "+ name
            + "\n\t\t\tdata: " + data
            + "\n\t\t\tinfo: " + e.getChangeInfo());

    if(name.equals(Names.inner_error) ) {
      msg("LOG print 'error by error'");
      throw (RuntimeException) b.getObject();
    }

    if ("not recive".equals(data)) {
      not_recive_when_off = false;
    }
    ++count;
  }


  @Override
  public void namingExceptionThrown(NamingExceptionEvent namingExceptionEvent) {
  }


  public static void main(String[] a) throws Exception {
    sub("Global Event");
    new TestEvent();
  }
}
