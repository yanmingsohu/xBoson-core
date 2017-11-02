/* CatfoOD 2017年11月2日 下午6:02:42 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

import com.xboson.util.Tool;

public class ConsoleOut implements ILogWriter {

	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		StringBuilder buf = new StringBuilder();
		
		buf.append(Tool.formatDate(d));
		buf.append(" [");
		buf.append(l.toString());
		buf.append("] [");
		buf.append(name);
		buf.append("]");
		
		for (int i=0; i<msg.length; ++i) {
			buf.append(' ');
			buf.append(msg[i]);
		}
		
		System.out.println(buf.toString());
	}

	
	@Override
	public void destroy() {
	}
}
