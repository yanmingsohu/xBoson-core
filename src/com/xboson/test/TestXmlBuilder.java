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
// 文件创建日期: 18-6-5 下午8:06
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestXmlBuilder.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.app.lib.XmlImpl;
import com.xboson.script.lib.JsOutputStream;
import com.xboson.util.StringBufferOutputStream;

import java.io.IOException;


public class TestXmlBuilder extends Test {

  @Override
  public void test() throws Throwable {
    basic();
  }


  public void basic() throws Exception {
    sub("basic");

    XmlImpl xml = new XmlImpl();
    StringBufferOutputStream buf = new StringBufferOutputStream();
    JsOutputStream jo = new JsOutputStream(buf);

    XmlImpl.XmlRoot root = xml.build(jo);
    XmlImpl.XmlTagWriter a = root.tag("a");
    XmlImpl.XmlTagWriter b = a.tag("b");
    XmlImpl.XmlTagWriter c = b.tag("c");

    b.tag("d").attr("type", "string");

    XmlImpl.XmlTagWriter txt = a.tag("txt");
    JsOutputStream text = txt.textWriter();
    text.write("<br/>");

    root.end();


    new Throws(IllegalStateException.class) {
      public void run() throws Throwable {
        b.text("bad");
      }
    };

    new Throws(IllegalStateException.class) {
      public void run() throws Throwable {
        a.attr("bad", "bad");
      }
    };

    String xmlstr = buf.toString();
    eq(xmlstr, "<a><b><c/><d type=\"string\"/></b><txt>&lt;br/&gt;</txt></a>", "xml");
    msg("ok", xmlstr);
  }


  public static void main(String[] av) throws IOException {
    new TestXmlBuilder();
  }

}
