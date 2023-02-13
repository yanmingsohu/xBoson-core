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
// 文件创建日期: 17-11-25 上午8:44
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ConverterInitialization.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.xboson.been.ResponseRoot;
import com.xboson.been.XmlDataMap;
import com.xboson.util.converter.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * xml/json 等转换器初始化列表, 不要直接调用来初始化转换器,
 * 应该使用 Tool 提供的方法来获取已经缓存的转换器.
 *
 * @see Tool#createXmlStream()
 * @see Tool#getAdapter(Class)
 */
public final class ConverterInitialization {


  public static Moshi createMoshi() {
    Moshi.Builder jsbuilded = new Moshi.Builder();
    JsonFactory xbosonFact = new JsonFactory();
    ConverterInitialization.initJSON(xbosonFact);
    jsbuilded.add(xbosonFact);
    return jsbuilded.build();
  }


  /**
   * 将所有可用转换器插入 XStream, 用于 xml 格式的转换
   *
   * @param xs XStream 对象实例
   */
  public static void initXml(com.thoughtworks.xstream.XStream xs) {
    xs.registerConverter(new XmlDataMapConverter());
    xs.registerConverter(ScriptObjectMirrorXmlConverter.me);
    xs.registerConverter(new ScriptObjectMirrorXmlConverter.WarpConverter());

    xs.addPermission(NoTypePermission.NONE);
    xs.addPermission(NullPermission.NULL);
    xs.addPermission(PrimitiveTypePermission.PRIMITIVES);

    xs.allowTypes(new Class[] {
            ScriptObjectMirrorJsonConverter.Warp.class,
            ScriptObjectMirror.class,
            ResponseRoot.class,
            XmlDataMap.class,
    });
  }


  /**
   * 将所有可用转换器插入 Moshi.Builder 中, 用于 json 格式的转换
   *
   * @param jsbuilded Moshi.Builder 对象实例
   */
  public static void initJSON(JsonFactory fact) {
    ScriptObjectMirrorJsonConverter.registerAdapter(fact);
    TimestampJsonConverter.registerAdapter(fact);
    new ExceptionConverter().register(fact);
    new MongoJsonConverter().register(fact);
    new MongoObjectIDConverter().register(fact);
    new BigDecimalConverter().register(fact);
    new BigIntegerConverter().register(fact);
    new StringBuilderJsonConverter().register(fact);
    new BytesConverter().register(fact);
    new BlockJsonConverter().registerAdapter(fact);
    new LinkedHashMapJsonConverter().register(fact);
    new JavaListMapJsonConverter().register(fact);
    //new NeoValueJsonConverter().register(fact);
  }


  /** 无必要 */
  private ConverterInitialization() {}


  public static class JsonFactory implements JsonAdapter.Factory {

    private Map<Type, JsonAdapter> types;


    private JsonFactory() {
      types = new HashMap<>();
    }

    @Nullable
    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> set, Moshi moshi) {
//    if (Map.class.isAssignableFrom(c)) {
//      return (JsonAdapter<E>) moshi.adapter(Map.class);
//    }
//    if (List.class.isAssignableFrom(c)) {
//      return (JsonAdapter<E>) moshi.adapter(List.class);
//    }
      return types.get(type);
    }

    public <T> void add(Type type, JsonAdapter<T> jsonAdapter) {
      types.put(type, jsonAdapter);
    }
  }
}
