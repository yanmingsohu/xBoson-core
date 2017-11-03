/* CatfoOD 2017年11月3日 下午4:14:35 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

import com.xboson.util.Tool;

public abstract class OutBase implements ILogWriter {

	/**
	 * 格式化数据到 add 对象, 末尾无换行
	 */
	public void format(Appendable add, Date d, Level l, String name, Object[] msg) {
		try {
			add.append(Tool.formatDate(d));
			add.append(" [");
			add.append(l.toString());
			add.append("] [");
			add.append(name);
			add.append("]");
			
			for (int i=0; i<msg.length; ++i) {
				add.append(' ');
				add.append(msg[i].toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
