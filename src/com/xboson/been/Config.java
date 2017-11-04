/* CatfoOD 2017年11月3日 下午12:55:05 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;


/** 
 * public 属性能死么 ?! getter/setter 烦不烦.
 */
public class Config {

	public static final String CONFIG_DIR 	= "/xBoson-config";
	public static final String CONFIG_FILE 	= "/config.json";
	public static final String LOG_DIR 			= "/logs";
	
	public String configFile				= null;
	public String configPath				= null;
	public String logPath						= null;
	public String home              = null;
	
	public String loggerWriterType	= null;
	public String sessionPassword		= null;
	public int    sessionTimeout		= 0;
	public String logLevel					= null;
	
	
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
