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
// 文件创建日期: 18-1-5 上午11:47
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/MongoJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.mongodb.client.MongoIterable;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.xboson.util.Tool;

import java.io.IOException;


public class MongoJsonConverter extends AbsJsonConverterHelper<MongoIterable> {

  @Override
  Class<MongoIterable> classType() {
    return MongoIterable.class;
  }


  @Override
  public MongoIterable fromJson(JsonReader jsonReader) throws IOException {
    throw new UnsupportedOperationException("fromJson");
  }


  @Override
  public void toJson(JsonWriter jsonWriter, MongoIterable mongoIterable)
          throws IOException {

    jsonWriter.beginArray();
    for (Object val : mongoIterable) {
      JsonAdapter ja = Tool.getAdapter(val.getClass());
      ja.toJson(jsonWriter, val);
    }
    jsonWriter.endArray();
  }
}
