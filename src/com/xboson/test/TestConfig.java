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
// 文件创建日期: 2017年11月3日 下午12:44:34
// 原始文件路径: xBoson/src/com/xboson/test/TestConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.squareup.moshi.JsonAdapter;
import com.thoughtworks.xstream.XStream;
import com.xboson.been.Config;
import com.xboson.j2ee.resp.XmlResponse;
import com.xboson.util.config.DefaultConfig;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import com.xboson.util.config.JsonConfig;
import com.xboson.util.config.YamlConfigImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


public class TestConfig extends Test {


	public void test() throws Exception {
    from_sys_config();
    json();
    yaml();
//    xml();
//    readJsonConfigToYaml();
  }


  // 工具方法, 非测试用例
  public void readJsonConfigToYaml() throws IOException {
	  sub("Read Json to Yaml");
    SysConfig sys = SysConfig.me();
    Config c = sys.readConfig();
//    msg(Tool.getAdapter(Config.class).toJson(c));
    YamlConfigImpl yc = new YamlConfigImpl();
    msg(yc.convert(c));
  }


  public void from_sys_config() throws IOException {
	  sub("Sys read config");
		SysConfig sys = SysConfig.me();
		msg( sys.getHomePath() );
		sys.checkConfigFiles();
		
		Config c = sys.readConfig();
		msg(c.configFile);
		msg(c.configPath);
		
		//ok(c.sessionPassword.equals("fdsevbvsx_fdsaf"), "bad session ps");
		ok(c.sessionTimeout > 0, "session timeout");
	}


  /**
   * 如果默认配置文件不存使用默认值创建这个文件
   */
	public void json() throws IOException {
	  sub("生成系统默认配置:");
    msg(line);

    Config c = new Config("~");
    DefaultConfig.setto(c);
    JsonConfig jc = new JsonConfig();

    String json = jc.convert(c);
    msg(json);
    msg(line);

    eq(c, jc.convert(json));
//    json = DefaultConfig.reomveComments(json);
//    msg(json);
//    msg(line);
  }


  public void yaml() throws Exception {
	  sub("YAML");
    YamlConfigImpl yc = new YamlConfigImpl();
    Config c = new Config("~");
    DefaultConfig.setto(c);

    String yaml = yc.convert(c);
    msg(yaml);

    Config rc = yc.convert(yaml);
    eq(c, rc);
  }


  public void xml() throws Exception {
	  sub("XML Config");
    Config c = new Config("~");
    DefaultConfig.setto(c);

    Writer out = new StringWriter();
    out.write('\n');
    out.write(XmlResponse.XML_HEAD);

    XStream xs = Tool.createXmlStream();
    xs.toXML(c, out);
    out.write('\n');

    String xml = out.toString();
    msg(xml);

    Config rc = (Config) xs.fromXML(xml);
    eq(c, rc);
  }


  public void eq(Config original, Config convert) {
    JsonAdapter<Config> ja = Tool.getAdapter(Config.class);
    eq(ja.toJson(original), ja.toJson(convert), "convert ok");
  }


  public static void main(String[] a) {
	  new TestConfig();
  }
}
