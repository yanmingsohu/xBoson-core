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
// 文件创建日期: 20-12-7 上午10:29
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/JavaListMapJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.xboson.been.XBosonException;
import com.xboson.util.ConverterInitialization;
import com.xboson.util.Tool;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class JavaListMapJsonConverter extends JsonAdapter {


  public void register(ConverterInitialization.JsonFactory f) {
    Map o = Collections.singletonMap(null, null);
    f.add(o.getClass(), this);
    Map u = Collections.unmodifiableMap(o);
    f.add(u.getClass(), this);
    f.add(HashMap.class, this);
  }


  @Nullable
  @Override
  public Object fromJson(JsonReader jsonReader) throws IOException {
    return null;
  }


  public void toJson(JsonWriter w, Object singletonMap) throws IOException {
    Map m = (Map) singletonMap;
    JsonAdapter ja = Tool.getAdapter(Map.class);
    ja.toJson(w, m);
  }
}
