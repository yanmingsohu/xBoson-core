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
// 文件创建日期: 17-11-18 下午7:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestXML.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.thoughtworks.xstream.XStream;
import com.xboson.been.ResponseRoot;
import com.xboson.j2ee.resp.XmlResponse;
import com.xboson.util.Tool;
import com.xboson.util.converter.XmlDataMapConverter;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


public class TestXML extends Test {


  public void test() throws Throwable {
    test_thread();
    test_resp();
  }


  public void test_resp() throws Throwable {
    XmlResponse x = new XmlResponse();

    Map<String, Object> ret_root = new ResponseRoot();
    ret_root.put("code", 0);
    ret_root.put("message", "ok");

    Map<Object, Object> a = new HashMap<>();
    a.put("xxx", 1);
    a.put("yyu", 2);
    ret_root.put("a", a);

    Writer out = new StringWriter();
    out.write('\n');
    out.write(x.XML_HEAD);

    XStream xs = Tool.createXmlStream();
    xs.toXML(ret_root, out);
    out.write('\n');

    String xmlstr = out.toString();
    Object o = xs.fromXML(xmlstr);
    msg("From Xml:", o);

    msg(xmlstr);
  }



  /**
   * XStream 是线程安全的
   */
  public void test_thread() {
    sub("Mutil XStream Thread Safe");
    final XStream xs = Tool.createXmlStream();
    TestData td = new TestData();
    td.change();
    msg("---------- XML:\n", xs.toXML(td));

    beginTime();
    Thread []ts = new Thread[1000];
    for (int i=0; i<ts.length; ++i) {
      ts[i] = new Thread(new Runnable() {
        public void run() {
          TestData td = new TestData();
          td.change();
          xs.toXML(td);
        }
      });
      ts[i].start();
    }

    for (int i=0; i<ts.length; ++i) {
      Tool.waitOver(ts[i]);
    }
    endTime(ts.length, "Thread over");
  }


  public static void main(String[] a) {
    new TestXML();
  }

}
