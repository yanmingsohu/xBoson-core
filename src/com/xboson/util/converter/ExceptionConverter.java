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
// 文件创建日期: 17-12-13 下午3:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/ExceptionConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.xboson.been.XBosonException;
import com.xboson.util.Tool;

import java.io.IOException;
import java.rmi.RemoteException;


public class ExceptionConverter extends AbsJsonConverterHelper<Exception> {

  public void register(Moshi.Builder builder) {
    builder.add(ClassCastException.class, this);
    builder.add(Exception.class, this);
    builder.add(XBosonException.class, this);
    builder.add(NullPointerException.class, this);
    builder.add(IllegalArgumentException.class, this);
    builder.add(NoSuchMethodException.class, this);
    builder.add(RemoteException.class, this);
  }


  @Override
  Class<Exception> classType() {
    return Exception.class;
  }


  @Override
  public void toJson(JsonWriter jsonWriter, Exception e) throws IOException {
    jsonWriter.beginObject();
    jsonWriter.name("message");
    jsonWriter.value(e.getMessage());
    jsonWriter.name("stack");
    jsonWriter.value(Tool.xbosonStack(e));
    if (e.getCause() != null) {
      jsonWriter.name("cause");
      jsonWriter.value(e.getCause().toString());
    }
    jsonWriter.endObject();
  }
}
