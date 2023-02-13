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
// 文件创建日期: 18-4-18 上午10:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/emu/EmuFilterConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.emu;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;


public class EmuFilterConfig implements FilterConfig {

  public ServletContext servlet_context;

  @Override
  public String getFilterName() {
    return null;
  }


  @Override
  public ServletContext getServletContext() {
    return servlet_context;
  }


  @Override
  public String getInitParameter(String s) {
    return null;
  }


  @Override
  public Enumeration<String> getInitParameterNames() {
    return null;
  }
}
