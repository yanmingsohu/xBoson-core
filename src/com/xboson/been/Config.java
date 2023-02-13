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
import com.xboson.util.Version;
import com.xboson.util.config.SerialFactory;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 不要删除 Config 对象中的属性, 除非世界末日.
 */
public class Config implements IBean {

  public static final String VERSION = Version.xBoson +".11";

  public String configVersion     = VERSION;

  public String configFile				= null;
  public String configPath				= null;
  public String logPath						= null;
  public String home              = null;
  public String remoteIpHeader    = null;
  public int    maxPostBody       = 0;
  public AppSelf appSelf          = null;

  public String rootUserName      = null;
  public String rootPassword			= null;
  public String rootPid           = null;

  public String loggerWriterType	= null;
  public String sessionPassword		= null;
  /** 分钟 */
  public int    sessionTimeout		= 0;
  public String logLevel					= null;
  public boolean debugService     = false;

  public String uiProviderClass   = null;
  public String uiUrl             = null;
  public boolean uiListDir        = false;
  public String uiWelcome         = null;
  public String[] uiRenderServer;

  public String nodeProviderClass = null;
  public String nodeUrl           = null;
  public String shellUrl          = null;

  public String chainPath         = null;
  public int    chainIncrement    = 1024;
  public boolean chainEnable      = false;

  public String[] sysTableList;
  public String[] shareAppList;

  public JedisPoolConfig jedispool;
  public DBPoolConfig dbpool;
  public ConnectConfig db;
  public RedisConfig redis;
  public MongoConfig mongodb;

  public short clusterNodeID;
  public String[] rpcIp;
  public String rpcIpMask;
  public int rpcPort;
  public boolean rpcUpnp;
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
    configPath	= home + SerialFactory.CONFIG_DIR;
    logPath 		= configPath + SerialFactory.LOG_DIR;
    configFile	= configPath + '/' + SerialFactory.get().fileName();
  }
}
