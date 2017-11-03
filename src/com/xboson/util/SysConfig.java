/* CatfoOD 2017年11月3日 下午12:44:17 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.xboson.been.Config;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

public class SysConfig {
	
	private static final String DEF_CONF_FILE = "./default-config.json";
	private static final SysConfig instance = new SysConfig();
	
	private Log log = LogFactory.create("SysConfig");
	private String homepath;
	private Config config;
	private boolean readed;

	
	private SysConfig() {
		initHomePath();
		readed = false;
		config = new Config(homepath);
	}
	
	
	public static SysConfig getInstance() {
		return instance;
	}
	
	
	private void initHomePath() {
		homepath = System.getenv("HOME");
		if (homepath != null) return;
		homepath = System.getenv("HOMEPATH");
		if (homepath != null) return;
		homepath = System.getenv("APPDATA");
		if (homepath != null) return;
		homepath = System.getenv("LOCALAPPDATA");
		if (homepath != null) return;
		homepath = System.getenv("ALLUSERSPROFILE");
		
		if (homepath == null) {
			throw new RuntimeException("Cannot init HOME path");
		}
	}
	
	
	public String getHomePath() {
		return homepath;
	}
	
	
	public Config readConfig() {
		if (!readed) {
			try {
				String str = Tool.readFromFile(config.configFile).toString();
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<Config> configAdapter = moshi.adapter(Config.class);
				Config run = configAdapter.fromJson(str);
				config.copy(run);
				readed = true;
				log.info("Read Config from", config.configFile);
			} catch(Exception e) {
				log.error("Read Config fail", e.getMessage());
			}
		}
		return config;
	}
	
	
	public void checkConfigFiles() {
		mkdirNotexists(config.configPath);
		mkdirNotexists(config.logPath);
		File cfile = new File(config.configFile);
		
		if (!cfile.exists()) {
			try {
				log.info("Copy config file", DEF_CONF_FILE, "=>", cfile);
				InputStream in = getClass().getResourceAsStream(DEF_CONF_FILE);
				FileOutputStream out = new FileOutputStream(cfile);
				Tool.copy(in, out, true);
			} catch(Exception e) {
				log.error(e.getMessage());
			}
		}
	}
	
	
	private void mkdirNotexists(String dirname) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			dir.mkdirs();
			log.info("Make dir", dirname);
		}
	}
	
	
	static public class Init implements ServletContextListener {

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
		}

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			SysConfig sys = SysConfig.getInstance();
			sys.checkConfigFiles();
			sys.readConfig();
		}
		
	}
}
