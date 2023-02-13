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
// 文件创建日期: 17-12-27 上午8:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/writer/WinConsoleOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class WinConsoleOut extends OutBase implements ILogWriter {

  private Map<Level, Ansi.Color> colors = new HashMap<>();


  public WinConsoleOut() {
    AnsiConsole.systemInstall();
    colors.put(Level.DEBUG, Ansi.Color.CYAN);
    colors.put(Level.INFO,  Ansi.Color.WHITE);
    colors.put(Level.WARN,  Ansi.Color.YELLOW);
    colors.put(Level.ERR,   Ansi.Color.RED);
    colors.put(Level.FATAL, Ansi.Color.MAGENTA);
  }


  @Override
  public void output(Date d, Level l, String name, Object[] msg) {
    StringBuilder buf = new StringBuilder();
    format(buf, d, l, name, msg);

    Ansi.Color color = colors.get(l);
    if (color == null) {
      System.out.println(buf.toString());
    } else {
      System.out.println(Ansi.ansi().fg(color).a(buf));
    }
  }


  @Override
  public void destroy(ILogWriter replace) {
    AnsiConsole.systemUninstall();
  }
}
