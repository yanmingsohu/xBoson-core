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
// 文件创建日期: 18-1-9 下午6:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/config/YamlConfigImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.config;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.xboson.been.Config;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.StringBufferOutputStream;

import java.io.IOException;


public class YamlConfigImpl extends AbsConfigSerialization {


  @Override
  public String convert(Config c) throws IOException {
    StringBufferOutputStream buf = new StringBufferOutputStream();
    YamlWriter w = new YamlWriter(buf.openWrite(), basicConfig());
    w.write(c);
    w.close();
    return addComments(buf.toString());
  }


  @Override
  public Config convert(String yaml) throws IOException {
    YamlReader r = new YamlReader(yaml);
    return r.read(Config.class);
  }


  public static YamlConfig basicConfig() {
    YamlConfig yc = new YamlConfig();
    yc.writeConfig.setKeepBeanPropertyOrder(true);
    yc.writeConfig.setWriteDefaultValues(true);
    yc.writeConfig.setEscapeUnicode(false);
    return yc;
  }


  /** YAML 支持注释不需要去掉就可以解析 */
  @Override
  public String reomveComments(String yaml) {
    return yaml;
  }


  @Override
  public String beginComment() {
    return "# ";
  }


  @Override
  public String endComment() {
    return IConstant.NULL_STR;
  }


  @Override
  public String find(String key) {
    return key + ':';
  }


  @Override
  public String fileName() {
    return "config.yaml";
  }
}
