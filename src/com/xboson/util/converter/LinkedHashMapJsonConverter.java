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
// 文件创建日期: 18-12-10 下午1:13
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/LinkedHashMapJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.xboson.util.Tool;

import java.io.IOException;
import java.util.LinkedHashMap;


public class LinkedHashMapJsonConverter extends AbsJsonConverterHelper<LinkedHashMap> {

  @Override
  Class<LinkedHashMap> classType() {
    return LinkedHashMap.class;
  }


  @Override
  public void toJson(JsonWriter w, LinkedHashMap map) throws IOException {
    w.beginObject();
    for (Object k : map.keySet()) {
      w.name(String.valueOf(k));
      Object v = map.get(k);
      if (v == null) {
        w.nullValue();
      } else {
        JsonAdapter ja = Tool.getAdapter(v.getClass());
        ja.toJson(w, v);
      }
    }
    w.endObject();
  }
}
