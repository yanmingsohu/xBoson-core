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
// 文件创建日期: 17-12-13 下午5:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/CloseableSet.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * 维护多个可关闭对象, 在 close() 时全部被关闭,
 * 即使有部分对象抛出异常, 也尽可能的调用全部对象的 close().
 */
public class CloseableSet implements AutoCloseable {

  private List<AutoCloseable> list;
  private Log log;


  public CloseableSet() {
    list = new ArrayList<>();
    log = LogFactory.create();
  }


  public<T extends AutoCloseable> T add(T c) {
    if (list == null) {
      throw new XBosonException.Closed("Closeable set");
    }
    list.add(c);
    return c;
  }


  @Override
  public void close() {
    for (int i=list.size()-1; i>=0; --i) {
      AutoCloseable c = list.get(i);
      try {
        c.close();
      } catch (Throwable t) {
        log.error("Close object", c, t.toString());
      }
    }
    list = null;
  }
}
