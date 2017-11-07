/* CatfoOD 2017年11月2日 下午6:02:42 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConsoleOut extends OutBase implements ILogWriter {

	private Map<Level, String> colors = new HashMap<>();


	public ConsoleOut() {
    colors.put(Level.DEBUG,"\u001b[;37m");
    colors.put(Level.INFO, "\u001b[;39m");
    colors.put(Level.WARN, "\u001b[;33m");
    colors.put(Level.ERR,  "\u001b[;31m");
    colors.put(Level.FATAL,"\u001b[;34m");
  }


	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		StringBuilder buf = new StringBuilder();
		format(buf, d, l, name, msg);

		String color = colors.get(l);
		System.out.println(color + buf.toString() + "\u001b[m");
	}

	
	@Override
	public void destroy(ILogWriter replace) {
	}
}
