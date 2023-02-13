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
// 文件创建日期: 17-11-21 上午9:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/container/Welcome.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.container;

import com.xboson.been.Config;
import com.xboson.util.SysConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;


/**
 * 根目录访问跳转
 *
 * @see com.xboson.init.Startup 配置到容器
 */
public class Welcome extends HttpServlet {

  private String welcome;


  @Override
  public void init() throws ServletException {
    super.init();
    Config cf = SysConfig.me().readConfig();
    welcome = cf.uiWelcome;
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    Writer w = resp.getWriter();
    w.write( req.getContextPath() );
    w.write( req.getRequestURI() );
    super.doGet(req, resp);
  }
}
