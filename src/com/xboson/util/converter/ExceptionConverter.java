////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-13 下午3:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/ExceptionConverter.java
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
import com.xboson.util.Tool;

import java.io.IOException;


public class ExceptionConverter {

  public static final ExceptionAdapter ea = new ExceptionAdapter();


  public static void registerAdapter(Moshi.Builder builder) {
    builder.add(ClassCastException.class, ea);
    builder.add(Exception.class, ea);
    builder.add(XBosonException.class, ea);
    builder.add(NullPointerException.class, ea);
  }


  static public class ExceptionAdapter extends JsonAdapter<Exception> {

    @Override
    public Exception fromJson(JsonReader jsonReader) throws IOException {
      throw new UnsupportedOperationException("fromJson Exception");
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
}
