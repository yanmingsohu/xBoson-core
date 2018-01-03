////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 下午12:55:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Config.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;


import com.xboson.db.ConnectConfig;
import com.xboson.db.DBPoolConfig;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 不要修改 Config 对象中的属性, 除非世界末日.
 */
public class Config implements IBean {

  public static final String CONFIG_DIR 	= "/xBoson-config";
  public static final String CONFIG_FILE 	= "/config.json";
  public static final String LOG_DIR 			= "/logs";

  public String configVersion     = "1.3.2";

  public String configFile				= null;
  public String configPath				= null;
  public String logPath						= null;
  public String home              = null;

  public String rootUserName      = null;
  public String rootPassword			= null;
  public String rootPid           = null;

  public String loggerWriterType	= null;
  public String sessionPassword		= null;
  public int    sessionTimeout		= 0; // 分钟
  public String logLevel					= null;
  public boolean debugService     = false;

  public String uiProviderClass   = null;
  public String uiUrl             = null;
  public boolean uiListDir        = false;
  public String uiWelcome         = null;

  public String nodeProviderClass = null;
  public String nodeUrl           = null;

  public String[] sysTableList;

  public JedisPoolConfig jedispool;
  public DBPoolConfig dbpool;
  public ConnectConfig db;
  public ConnectConfig redis;
  public MongoConfig mongodb;

  public byte clusterNodeID;
  public boolean enableUploadClear;
  public boolean enableSessionClear;
  public boolean enableUIFileSync;
  public boolean enableNodeFileSync;


	/**
	 * public 属性能死么 ? 并不能 !
	 */
	public Config() {
  }


  public Config(String home) {
    setHome(home);
  }


  public void setHome(String home) {
    this.home		= home;
    configPath	= home + CONFIG_DIR;
    logPath 		= configPath + LOG_DIR;
    configFile	= configPath + CONFIG_FILE;
  }
}
