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
// 文件创建日期: 18-1-19 下午3:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestMasquerade.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.fs.redis.IRedisFileSystemProvider;
import com.xboson.fs.ui.UIFileFactory;
import com.xboson.j2ee.ui.TemplateEngine;
import com.xboson.j2ee.emu.EmuServletRequest;
import com.xboson.j2ee.emu.EmuServletResponse;


public class TestMasquerade extends Test {

  //
  // 这个测试会抛出异常, 因为初始化时 Startup 没有 ServletContext
  //
  @Override
  public void test() throws Throwable {
    page();
  }


  private void page() throws Throwable {
    sub("Request page");
    IRedisFileSystemProvider uifs = UIFileFactory.open();
    TemplateEngine te = new TemplateEngine(uifs);

    String[] pages = new String[] {
            "/face/t/paas/api-doc/index.htm",
            "/face/t/paas/mdms/datadictD/index.htm",
    };

    for (String page : pages) {
      EmuServletResponse resp = new EmuServletResponse();
      EmuServletRequest req = new EmuServletRequest();
      req.requestUriWithoutContext = page;
      te.service(req, resp);
    }
  }


  public static void main(String[] a) {
    new TestMasquerade();
  }

}
