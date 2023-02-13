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
// 文件创建日期: 17-12-27 上午9:17
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/writer/LinuxConsoleOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class LinuxConsoleOut extends OutBase implements ILogWriter {

  private Map<Level, String> colors = new HashMap<>();
  private String END_COLOR;


  public LinuxConsoleOut() {
    colors.put(Level.DEBUG, "\u001b[;37m");
    colors.put(Level.INFO, "\u001b[;39m");
    colors.put(Level.WARN, "\u001b[;33m");
    colors.put(Level.ERR, "\u001b[;31m");
    colors.put(Level.FATAL, "\u001b[;34m");
    END_COLOR = "\u001b[m";
  }


  @Override
  public void output(Date d, Level l, String name, Object[] msg) {
    StringBuilder buf = new StringBuilder();
    format(buf, d, l, name, msg);

    String color = colors.get(l);
    if (color == null) {
      System.out.println(buf.toString());
    } else {
      System.out.println(color + buf.toString() + END_COLOR);
    }
  }


  @Override
  public void destroy(ILogWriter replace) {
  }
}
