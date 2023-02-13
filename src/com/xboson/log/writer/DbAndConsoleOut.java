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
// 文件创建日期: 17-11-20 下午4:25
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/writer/DbAndConsoleOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;

import java.io.IOException;
import java.util.Date;


public class DbAndConsoleOut extends OutBase implements ILogWriter {

  private DbOut db;
  private ConsoleOut cons;


  public DbAndConsoleOut() {
    db = new DbOut();
    cons = new ConsoleOut();
  }


  @Override
  public void output(Date d, Level l, String name, Object[] msg) {
    cons.output(d, l, name, msg);
    db.output(d, l, name, msg);
  }


  @Override
  public void destroy(ILogWriter replace) {
    cons.destroy(replace);
    db.destroy(replace);
  }
}
