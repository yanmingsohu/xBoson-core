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
// 文件创建日期: 2017年11月3日 下午4:11:40
// 原始文件路径: xBoson/src/com/xboson/log/FileOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;


public class FileOut extends OutBase implements ILogWriter {

  private static final String line = "\r\n";
  private static final String logFileNameFormat = "yyyy-MM-dd_HH";
  private static final long checkPeriod = 1 * 60 * 1000;
  private static final long resetSize = 10 * 1024 * 1024;

  private File currentFile;
  private Writer writer;
  private Timer checksize;


  public FileOut() throws IOException {
    currentFile = logFile();
    switchOutFile();
    checksize = new Timer(true);
    checksize.schedule(new CheckSize(), checkPeriod, checkPeriod);
  }


  private File logFile() {
    SimpleDateFormat f = new SimpleDateFormat(logFileNameFormat);
    String name = SysConfig.me().readConfig().logPath;
    name += "/" + f.format(new Date()) + "h.log";
    return new File(name);
  }


  @Override
  public synchronized void output(Date d, Level l, String name, Object[] msg) {
    try {
      //
      // 因为没有在 Log 中做线程同步, 写出时, 该对象可能已经关闭而抛出异常,
      // 考虑到性能原因, 接受在切换输出时丢失部分日志.
      //
      format(writer, d, l, name, msg);
      writer.append(line);
    } catch(Exception e) {
      nolog("File Writer Fail: " + e);
    }
  }


  @Override
  public synchronized void destroy(ILogWriter replace) {
    checksize.cancel();
    Tool.close(writer);
  }


  private synchronized void switchOutFile() throws IOException {
    OutputStream o = new FileOutputStream(currentFile, true);
    writer = new OutputStreamWriter(o, IConstant.CHARSET);
  }


  private class CheckSize extends TimerTask {
    private int num = 0;

    public void run() {
      if (currentFile.length() > resetSize) {
        synchronized(FileOut.this) {
          Tool.close(writer);

          File rename;
          do {
            rename = new File(currentFile.getPath() + '.' + num);
            ++num;
          } while (rename.exists());

          Tool.pl("Log output file switch", currentFile, "->", rename);
          currentFile.renameTo(rename);

          try {
            switchOutFile();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
