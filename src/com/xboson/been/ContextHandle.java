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
// 文件创建日期: 18-5-14 上午8:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ContextHandle.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContextHandle {

  public final List<String> handle;
  public final List<Thread> related;
  public final Thread main;


  public ContextHandle(Thread main) {
    if (main == null) {
      throw new NullPointerException("Thread main");
    }
    this.main    = main;
    this.handle  = Collections.synchronizedList(new ArrayList<>());
    this.related = Collections.synchronizedList(new ArrayList<>());
  }


  public void clear() {
    handle.clear();
    related.clear();
  }
}
