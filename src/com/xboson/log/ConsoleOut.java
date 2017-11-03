/* CatfoOD 2017年11月2日 下午6:02:42 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

public class ConsoleOut extends OutBase implements ILogWriter {

	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		StringBuilder buf = new StringBuilder();
		format(buf, d, l, name, msg);
		System.out.println(buf.toString());
	}

	
	@Override
	public void destroy(ILogWriter replace) {
	}
}
