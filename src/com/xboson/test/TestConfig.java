////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 下午12:44:34
// 原始文件路径: xBoson/src/com/xboson/test/TestConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import com.squareup.moshi.JsonWriter;
import com.xboson.been.Config;
import com.xboson.db.DBPoolConfig;
import com.xboson.util.DefaultConfig;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;
import okio.Buffer;
import redis.clients.jedis.JedisPoolConfig;


public class TestConfig extends Test {


	public void test() throws IOException {
    show_default_config();
	  test1();
  }


  public void test1() throws IOException {
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
	public void show_default_config() throws IOException {
	  sub("生成系统默认配置:");
    System.out.println(line);

    final Buffer buffer = new Buffer();
    final JsonWriter jsonWriter = JsonWriter.of(buffer);
    jsonWriter.setIndent("  ");

    Config c = new Config();
    DefaultConfig.setto(c);
    Tool.getAdapter(Config.class).toJson(jsonWriter, c);

    byte[] bytes = buffer.readByteArray();
    System.out.println(new String(bytes));
    System.out.println(line);
  }


  public static void main(String[] a) {
	  new TestConfig();
  }
}
