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
// 文件创建日期: 2017年11月2日 下午6:02:42
// 原始文件路径: xBoson/src/com/xboson/log/ConsoleOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.LogFactory;
import com.xboson.log.OutBase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConsoleOut extends OutBase implements ILogWriter {

  private final OutBase outImpl;


  public ConsoleOut() {
    boolean ansi_color = false;

    String encoding = System.getProperty("sun.stderr.encoding");
    if (encoding != null) {
      ansi_color = !encoding.equals("ms936");
    } else {
      ansi_color = true;
    }

    if (ansi_color) {
      outImpl = new LinuxConsoleOut();
    } else {
      outImpl = new WinConsoleOut();
    }
  }


  @Override
  public void output(Date d, Level l, String name, Object[] msg) {
    outImpl.output(d, l, name, msg);
  }


  @Override
  public void destroy(ILogWriter replace) {
    outImpl.destroy(replace);
  }
}
