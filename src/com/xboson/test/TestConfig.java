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
		ok(c.sessionTimeout == 30, "bad session timeout");

		sys.readDefaultConfig();
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
    default_config(c);
    Tool.getAdapter(Config.class).toJson(jsonWriter, c);

    byte[] bytes = buffer.readByteArray();
    System.out.println(new String(bytes));
    System.out.println(line);

    URL url = getClass().getResource("/com/xboson/util");

    File f = new File(url.getFile() +"/"+ SysConfig.CONF_FILE_NAME);
    if (f.exists()) {
      msg("The config file", f, "is exists");
    } else {
      FileOutputStream out = new FileOutputStream(f);
      out.write(bytes);
      out.close();
      sub("Write to", f);
    }
  }


  /**
   * 默认配置文件设置
   */
  public void default_config(Config c) {
    c.loggerWriterType = "ConsoleOut";
    c.logLevel = "all";
    c.sessionTimeout = 30;
    c.sessionPassword = randomString(20);
    c.redis_host = "localhost";
    c.debugService =  false;

    JedisPoolConfig j = c.jedispool = new JedisPoolConfig();
    j.setMaxIdle(10);
    j.setMinIdle(0);
    j.setMaxTotal(200);

    DBPoolConfig d = c.dbpool = new DBPoolConfig();

    //
    // 允许创建资源的最大数量,默认值 8,-1 代表无数量限制
    //
    d.setMaxTotal(2000);

    //
    // 默认值 true ,当资源耗尽时,是否阻塞等待获取资源
    //
    d.setBlockWhenExhausted(true);

    //
    // 获取资源时的等待时间,单位毫秒.当 blockWhenExhausted 配置为 true 时,
    // 此值有效. -1 代表无时间限制,一直阻塞直到有可用的资源.
    //
    d.setMaxWaitMillis(3000);

    //
    // 默认值 false ,当设置为true时,每次从池中获取资源时都会调用
    // factory.validateObject() 方法
    //
    d.setTestOnBorrow(true);
    d.setTestOnCreate(false);
    d.setTestOnReturn(false);

    //
    // 回收资源线程的执行周期,单位毫秒.默认值 -1 ,-1 表示不启用线程回收资源
    //
    d.setTimeBetweenEvictionRunsMillis((long)(1 * 3600e3));

    //
    // 设置为 true 时,当回收策略返回false时,
    // 则调用 factory.activateObject()和factory.validateObject()
    //
    d.setTestWhileIdle(true);

    //
    // 资源回收线程执行一次回收操作,回收资源的数量.默认值 3,
    //
    d.setNumTestsPerEvictionRun(99);
  }


  public static void main(String[] a) {
	  new TestConfig();
  }
}
