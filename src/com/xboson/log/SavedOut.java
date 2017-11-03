/* CatfoOD 2017年11月3日 下午2:12:33 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 记录日志, 直到被取代时, 将所有日志发送给取代者
 */
public class SavedOut implements ILogWriter {
	
	private Deque<Data> saved = new LinkedList<Data>();

	
	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		saved.addLast(new Data(d, l, name, msg));
	}

	
	@Override
	public void destroy(ILogWriter replace) {
		while (!saved.isEmpty()) {
			Data d = saved.pollFirst();
			replace.output(d.d, d.l, d.n, d.m);
		}
		saved = null;
	}

	
	static class Data {
		Date d;
		Level l;
		String n;
		Object[] m;
		
		Data(Date d, Level l, String n, Object[] m) {
			this.d = d;
			this.l = l;
			this.n = n;
			this.m = m;
		}
	}
}
