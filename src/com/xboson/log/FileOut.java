/* CatfoOD 2017年11月3日 下午4:11:40 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
		String name = SysConfig.getInstance().readConfig().logPath;
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
