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
// 文件创建日期: 17-11-24 下午12:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ScriptObjectMirrorJsonConverter.java
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
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.xboson.app.lib.RuntimeUnitImpl;
import com.xboson.util.ConverterInitialization;
import com.xboson.util.Tool;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeError;
import jdk.nashorn.internal.objects.NativeTypeError;

import java.io.IOException;
import java.util.Iterator;


/**
 * ScriptObjectMirror 不能很好的转换为 json, 将该对象包装,
 * 在 json 化的时候使用定制的方法来输出.
 */
public class ScriptObjectMirrorJsonConverter {

  public static final WarpAdapter warpAdapter = new WarpAdapter();
  public static final SOMAdapter jsAdapter = new SOMAdapter();


  public static void registerAdapter(Moshi.Builder builder) {
    builder.add(Warp.class, warpAdapter);
    builder.add(ScriptObjectMirror.class, jsAdapter);
  }


  public static void registerAdapter(ConverterInitialization.JsonFactory f) {
    f.add(Warp.class, warpAdapter);
    f.add(ScriptObjectMirror.class, jsAdapter);
  }


  /**
   * 包装器转换器
   */
  static public class WarpAdapter extends JsonAdapter<Warp> {

    @Override
    public void toJson(JsonWriter jsonWriter, Warp w)
            throws IOException {
      jsAdapter.toJson(jsonWriter, w.jsobj);
    }


    @Override
    public Warp fromJson(JsonReader jsonReader) throws IOException {
      throw new UnsupportedOperationException("fromJson");
    }
  }


  /**
   * ScriptObjectMirror 转换器
   */
  static public class SOMAdapter extends JsonAdapter<ScriptObjectMirror> {

    @Override
    public void toJson(JsonWriter jsonWriter, ScriptObjectMirror jsobj)
            throws IOException {
      if (jsobj.isFunction() || RuntimeUnitImpl.isNull(jsobj)) {
        jsonWriter.nullValue();
        return;
      }

      jsonWriter.setLenient(true);
      String cname = jsobj.getClassName();
      if (cname.indexOf("Error") >= 0) {
        Object stack = jsobj.getMember("stack");
        if (stack != null) {
          jsonWriter.beginObject();
          jsonWriter.name("error");
          jsonWriter.value(String.valueOf(stack));
          jsonWriter.name("type");
          jsonWriter.value(cname);
          jsonWriter.endObject();
          return;
        }
      }

      if (jsobj.isArray()) {
        isArray(jsonWriter, jsobj);
      } else {
        isMap(jsonWriter, jsobj);
      }
    }


    private void isArray(JsonWriter jsonWriter, ScriptObjectMirror jsobj)
            throws IOException {
      jsonWriter.beginArray();
      int len = jsobj.size();

      for (int i = 0; i < len; ++i) {
        Object val = jsobj.getSlot(i);

        if (val != null) {
          JsonAdapter ja = Tool.getAdapter(val.getClass());
          ja.toJson(jsonWriter, val);
        } else {
          jsonWriter.nullValue();
        }
      }

      jsonWriter.endArray();
    }


    private void isMap(JsonWriter jsonWriter, ScriptObjectMirror jsobj)
            throws IOException {
      jsonWriter.beginObject();
      Iterator<String> keys = jsobj.keySet().iterator();

      while (keys.hasNext()) {
        String name = keys.next();
        Object val = jsobj.get(name);
        jsonWriter.name(name);

        if (val != null) {
          JsonAdapter ja = Tool.getAdapter(val.getClass());
          ja.toJson(jsonWriter, val);
        } else {
          jsonWriter.nullValue();
        }
      }

      jsonWriter.endObject();
    }

    @Override
    public ScriptObjectMirror fromJson(JsonReader jsonReader)
            throws IOException {
      throw new UnsupportedOperationException("fromJson");
    }
  }


  /**
   * 对象包装器, 帮助深层对象的 json(xml) 化,
   * 解决当 ScriptObjectMirror 在 map 中时无法被正确的 Adapter 感知.
   * <br/><br/>
   * XStream 在反序列化时, 根据主节点名称来调用对应的转换器.
   */
  @XStreamAlias("js-object-root")
  static public class Warp {
    ScriptObjectMirror jsobj;

    public Warp(Object jsobj) {
      this.jsobj = (ScriptObjectMirror) jsobj;
    }


    public ScriptObjectMirror getWarpedObject() {
      return jsobj;
    }
  }


  /**
   * 使用内部类而不是对象.
   *
   * @see Warp
   * @see
   */
  private ScriptObjectMirrorJsonConverter() {}
}
