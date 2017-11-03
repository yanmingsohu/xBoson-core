/* CatfoOD 2017年11月3日 下午4:19:45 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.io.IOException;
import java.util.Date;

public class FileAndConsoleOut implements ILogWriter {
	
	private FileOut file;
	private ConsoleOut cons;
	
	
	public FileAndConsoleOut() {
		try {
			file = new FileOut();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cons = new ConsoleOut();
	}

	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		cons.output(d, l, name, msg);
		if (file != null) {
			file.output(d, l, name, msg);
		}
	}

	@Override
	public void destroy(ILogWriter replace) {
		cons.destroy(replace);
		file.destroy(replace);
	}

}
