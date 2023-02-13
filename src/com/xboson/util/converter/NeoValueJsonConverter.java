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
// 文件创建日期: 20-12-7 上午11:46
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/NeoValueJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.xboson.util.Tool;
import org.neo4j.driver.Value;

import java.io.IOException;


public class NeoValueJsonConverter extends AbsJsonConverterHelper<Value> {

  @Override
  Class<Value> classType() {
    return org.neo4j.driver.Value.class;
  }


  public void toJson(JsonWriter w, Value v) throws IOException {
    if (v.isNull()) {
      w.nullValue();
    }
    else if (v.isTrue()) {
      w.value(true);
    }
    else if (v.isFalse()) {
      w.value(false);
    } else {
      Object o = v.asObject();
      JsonAdapter ja = Tool.getAdapter(o.getClass());
      ja.toJson(w, o);
    }
  }
}
