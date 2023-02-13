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
// 文件创建日期: 2017年11月3日 下午4:14:35
// 原始文件路径: xBoson/src/com/xboson/log/OutBase.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

import java.io.IOException;
import java.util.Date;

import com.xboson.been.XBosonException;
import com.xboson.util.Tool;

public abstract class OutBase implements ILogWriter {

  /**
   * 格式化数据到 add 对象, 末尾无换行
   */
  public void format(Appendable add, Date d, Level l, String name, Object[] msg) {
    try {
      add.append(Tool.formatDate(d));
      add.append(" [");
      add.append(l.toString());
      add.append("] [");
      add.append(name);
      add.append("]");

      for (int i = 0; i < msg.length; ++i) {
        add.append(' ');
        add.append("" + msg[i]);
      }
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  public static void nolog(String msg) {
    System.out.println("::NO-LOG> " + msg);
  }
}
