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
// 文件创建日期: 18-1-9 下午5:50
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/config/JsonConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.config;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.xboson.been.Config;
import com.xboson.util.Tool;

import java.io.IOException;


public class JsonConfig extends AbsConfigSerialization {

  @Override
  public Config convert(String str) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Config> configAdapter = moshi.adapter(Config.class);
    return configAdapter.fromJson(reomveComments(str));
  }


  @Override
  public String convert(Config config) throws IOException {
    return Tool.beautifyJson(Config.class, config);
  }

  /**
   * 删除 json 中的注释, 返回标准 json 字符串
   */
  public String reomveComments(String json) {
    return json.replaceAll("/\\*.*\\*/", "");
  }


  @Override
  public String beginComment() {
    return "/* ";
  }


  @Override
  public String endComment() {
    return " */";
  }


  @Override
  public String find(String key) {
    return '"' + key + '"';
  }


  @Override
  public String fileName() {
    return "config.json";
  }
}
