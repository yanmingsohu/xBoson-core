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
// 文件创建日期: 18-5-30 下午3:59
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/IConverter.java
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
import com.xboson.been.XBosonException;
import com.xboson.util.ConverterInitialization;

import java.io.IOException;
import java.lang.reflect.Constructor;


/**
 * JsonAdapter 的默认实现, 如果 T 类型需要转换为 JSON 字符串则继承该对象,
 * 实现后必须在 ConverterInitialization 中注册方可生效.
 *
 * @see com.xboson.util.ConverterInitialization
 */
public abstract class AbsJsonConverterHelper<T> extends JsonAdapter<T> {


  /**
   * 该方法默认注册 T 类型的适配器
   */
//  public void register(Moshi.Builder builder) {
//    builder.add(classType(), this);
//  }


  public void register(ConverterInitialization.JsonFactory f) {
    f.add(classType(), this);
  }


  /**
   * 返回 T 类型的 Class 对象
   */
  abstract Class<T> classType();


  /**
   * 如果 T 有一个带有 String 参数的构造函数, 则默认实现可以满足需要
   * @see JsonAdapter#fromJson
   */
  @Override
  public T fromJson(JsonReader jsonReader) throws IOException {
    try {
      Constructor<T> creator = classType().getConstructor(String.class);
      T obj = creator.newInstance(jsonReader.nextString());
      return obj;
    } catch (Exception e) {
      throw new XBosonException(e);
    }
  }


  /**
   * 如果 T 的 toString() 方法就是 json 序列化则默认实现可以满足需要.
   * @see JsonAdapter#toJson
   */
  @Override
  public void toJson(JsonWriter jsonWriter, T obj)
          throws IOException {
    jsonWriter.value(obj.toString());
  }

}
