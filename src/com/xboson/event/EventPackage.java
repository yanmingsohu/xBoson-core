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
// 文件创建日期: 17-11-12 上午10:19
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/event/EventPackage.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.event;


import com.squareup.moshi.JsonAdapter;
import com.xboson.util.Tool;

import java.io.IOException;
import java.util.UUID;

public class EventPackage {

  private static final JsonAdapter<EventPackage>
          ad = Tool.getAdapter(EventPackage.class);

  public Object data;
  public int    type;
  public String info;
  public long   from;
  public String className;


  public EventPackage(Object data, int type, String info, long from) {
    this.type = type;
    this.info = info;
    this.from = from;
    // 数据被再次编码为 string, 并保留类型
    if (data != null) {
      this.data = Tool.getAdapter((Class) data.getClass()).toJson(data);
      this.className = data.getClass().getName();
    }
  }


  public String tojson() {
    return ad.toJson(this);
  }


  /**
   * 解析数据并还原为原始类型
   */
  public void parseData() {
    //
    // 为安全而过滤了包名, 但是这并不起作用, 反而增加了麻烦
    // && className.indexOf("com.xboson.") == 0
    //
    if (className != null && data != null) {
      try {
        Class data_class = Class.forName(className);
        data = Tool.getAdapter(data_class).fromJson(data.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }


  public static EventPackage fromjson(String str) throws IOException {
    return ad.fromJson(str);
  }
}
