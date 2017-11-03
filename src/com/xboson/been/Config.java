/* CatfoOD 2017年11月3日 下午12:55:05 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;


public class Config {

	public static final String CONFIG_DIR 	= "/xBoson-config";
	public static final String CONFIG_FILE 	= "/config.json";
	public static final String LOG_DIR 			= "/logs";
	
	/** public 能死么 ? */
	public String configFile				= null;
	public String configPath				= null;
	public String logPath						= null;
	
	public String loggerWriterType	= null;
	public String sessionPassword		= null;
	public int    sessionTimeout		= 0;
	
	
	public Config() {
	}
	
	
	public Config(String home) {
		configPath	= home + CONFIG_DIR;
		logPath 		= configPath + LOG_DIR;
		configFile	= configPath + CONFIG_FILE;
	}
	
	
	public void copy(Config c) {
		loggerWriterType = c.loggerWriterType;
		sessionPassword  = c.sessionPassword;
		sessionTimeout   = c.sessionTimeout;
	}
}
