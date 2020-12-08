////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
