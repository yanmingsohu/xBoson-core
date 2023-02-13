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
// 文件创建日期: 18-5-26 下午8:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/writer/LogWriterLoader.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.been.Config;
import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;
import com.xboson.util.Tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 该输出类会解析配置文件, 并设置输出,
 */
public class LogWriterLoader extends OutBase implements ILogWriter {

  private ILogWriter[] outArr;


  /**
   * 解析配置文件设置日志输出, 支持将多个输出器组合.
   */
  public static ILogWriter getLogWriter(Config cfg) {
    List<ILogWriter> out = new ArrayList<>();
    String types = cfg.loggerWriterType;

    if (Tool.isNulStr(types)) {
      return new ConsoleOut();
    }

    String[] ts = types.split(",");
    for (String t : ts) {
      if (! Tool.isNulStr(t)) {
        try {
          Class<?> cl = Class.forName("com.xboson.log.writer." + t.trim());
          ILogWriter lw = (ILogWriter) cl.newInstance();
          out.add(lw);
          nolog("Load log writer: "+ t);
        } catch (Exception e) {
          nolog("Load log writer '"+ t +"' Get fail: "+ e.getMessage());
        }
      }
    }

    if (out.size() < 1) {
      return new ConsoleOut();
    }

    return new LogWriterLoader(out);
  }


  private LogWriterLoader(List<ILogWriter> out) {
    this.outArr = out.toArray(new ILogWriter[out.size()]);
  }


  @Override
  public void output(Date d, Level l, String name, Object[] msg) {
    for (int i=0; i<outArr.length; ++i) {
      outArr[i].output(d, l, name, msg);
    }
  }


  @Override
  public void destroy(ILogWriter replace) {
    for (int i=0; i<outArr.length; ++i) {
      outArr[i].destroy(replace);
    }
  }
}
