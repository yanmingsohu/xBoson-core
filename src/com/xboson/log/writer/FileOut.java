////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月3日 下午4:11:40
// 原始文件路径: xBoson/src/com/xboson/log/FileOut.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.xboson.log.ILogWriter;
import com.xboson.log.Level;
import com.xboson.log.OutBase;
import com.xboson.util.SysConfig;
import com.xboson.util.Tool;


public class FileOut extends OutBase implements ILogWriter {
	
	private static final String line = "\r\n";
	private static final String logFileNameFormat = "yyyy-MM-dd HH";
	private static final long checkPeriod = 1 * 60 * 1000;
	private static final long resetSize = 10 * 1024 * 1024;
	
	private File currentFile;
	private FileWriter out;
	private Timer checksize;
	

	public FileOut() throws IOException {
		currentFile = logFile();
		out = new FileWriter(currentFile, true);
		checksize = new Timer(true);
		checksize.schedule(new CheckSize(), checkPeriod, checkPeriod);
	}
	
	
	private File logFile() {
		SimpleDateFormat f = new SimpleDateFormat(logFileNameFormat);
		String name = SysConfig.me().readConfig().logPath;
		name += "/" + f.format(new Date()) + "h.log";
		return new File(name);
	}


	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		synchronized (out) {
			format(out, d, l, name, msg);
			try {
				out.append(line);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void destroy(ILogWriter replace) {
		checksize.cancel();
		Tool.close(out);
	}

	
	private class CheckSize extends TimerTask {
		private int num = 0;
		
		public void run() {
			if (currentFile.length() > resetSize) {
				Tool.close(out);
				
				File rename;
				do {
					rename = new File(currentFile.getPath() + '.' + num);
					++num;
				} while (rename.exists());
				
				currentFile.renameTo(rename);
				
				try {
					out = new FileWriter(currentFile, true);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
