////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 下午12:44:17
// 原始文件路径: xBoson/src/com/xboson/util/SysConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.xboson.been.Config;
import com.xboson.event.GlobalEvent;
import com.xboson.event.Names;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import okio.Buffer;


public class SysConfig {

	private static SysConfig instance;

	private Log log = LogFactory.create("SysConfig");
	private String homepath;
	private Config config;
	private boolean readed;

	
	private SysConfig() {
		initHomePath();
		readed = false;
		config = new Config(homepath);

    DefaultConfig.setto(config);
		checkConfigFiles();
		readConfig(config.configFile);

		if (!readed) {
			// 日志处于不可用状态
			System.out.println("System Config Fail, exit() !");
			System.exit(1);
		}

		GlobalEvent.me().emit(Names.config, config);

		log.info("Initialization Success");
	}
	
	
	public synchronized static SysConfig me() {
		if (instance == null) {
			instance = new SysConfig();
		}
		return instance;
	}
	
	
	private void initHomePath() {
		homepath = System.getenv("HOME");
		if (homepath != null) return;
    homepath = System.getProperty("user.home");
    if (homepath != null) return;
		homepath = System.getenv("HOMEPATH");
		if (homepath != null) return;
		homepath = System.getenv("APPDATA");
		if (homepath != null) return;
		homepath = System.getenv("LOCALAPPDATA");
		if (homepath != null) return;
		homepath = System.getenv("ALLUSERSPROFILE");
		
		if (homepath == null) {
			System.err.println(System.getProperties());
			throw new RuntimeException("Cannot init HOME path");
		}
	}
	
	
	public String getHomePath() {
		return homepath;
	}
	
	
	public Config readConfig(String config_file) {
		if (!readed) {
			String str = null;
			try {
				str = Tool.readFromFile(config_file).toString();
				setConfigUseJson(str);
				log.info("Read Config from", config_file);

			} catch(Exception e) {
        System.err.println("Config file context: " + str);
				System.err.println("Read LoginUser Config:" + e);
			}
		}
		return config;
	}


	public Config readConfig() {
		return readConfig(config.configFile);
	}


	private void setConfigUseJson(String str) throws IOException {
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<Config> configAdapter = moshi.adapter(Config.class);

		Config run = configAdapter.fromJson(str);
		if (config.configVersion.compareTo(run.configVersion) > 0) {
		  log.warn("Configuration files need to be upgraded",
              run.configVersion, "<", config.configVersion);
    }

		run.setHome(config.home);
		config = run;
		readed = true;
	}

	
	public void checkConfigFiles() {
		mkdirNotexists(config.configPath);
		mkdirNotexists(config.logPath);
		File cfile = new File(config.configFile);
		
		if (!cfile.exists()) {
			generateDefaultConfigFile(config);
		}
	}


	public void generateDefaultConfigFile(Config config) {
		try {
			File cfile = new File(config.configFile);
			log.info("Generate config file", cfile);

			final Buffer buffer = new Buffer();
			final JsonWriter jsonWriter = JsonWriter.of(buffer);
			jsonWriter.setIndent("  ");

			Tool.getAdapter(Config.class).toJson(jsonWriter, config);

			try (FileOutputStream out = new FileOutputStream(cfile)) {
				out.write(buffer.readByteArray());
			}

			// 如果配置文件是程序生成的, 则认为配置已经读取完成.
			readed = true;
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	
	private void mkdirNotexists(String dirname) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			dir.mkdirs();
			log.info("Make dir", dirname);
		}
	}
}
