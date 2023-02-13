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
// 文件创建日期: 17-12-9 下午1:15
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/TimestampJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.xboson.util.ConverterInitialization;
import com.xboson.util.Tool;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;


public class TimestampJsonConverter {

  public static void registerAdapter(Moshi.Builder builder) {
    builder.add(Timestamp.class, new TimestampAdapter());
    builder.add(Date.class, new DateAdapter());
  }


  public static void registerAdapter(ConverterInitialization.JsonFactory f) {
    f.add(Timestamp.class, new TimestampAdapter());
    f.add(Date.class, new DateAdapter());
  }


  static public class TimestampAdapter extends JsonAdapter<Timestamp> {

    @Override
    public Timestamp fromJson(JsonReader jsonReader) throws IOException {
      throw new UnsupportedOperationException("fromJson Timestamp");
    }


    @Override
    public void toJson(JsonWriter jsonWriter, Timestamp timestamp)
            throws IOException {
      jsonWriter.value(Tool.formatDate(timestamp));
    }
  }


  static public class DateAdapter extends JsonAdapter<Date> {

    @Override
    public Date fromJson(JsonReader jsonReader) throws IOException {
      throw new UnsupportedOperationException("fromJson Timestamp");
    }


    @Override
    public void toJson(JsonWriter jsonWriter, Date date) throws IOException {
      jsonWriter.value(Tool.formatDate(date));
    }
  }
}
