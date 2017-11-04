/* CatfoOD 2017年11月2日 下午6:00:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

/**
 * 日志输出
 */
public interface ILogWriter {

	/**
	 * 压入新的日志条目
	 */
	void output(Date d, Level l, String name, Object[] msg);
	
	/**
	 * 销毁当前日志
	 * @param repleased - 替换当前输出器的新输出器, 可能为 null
	 */
	void destroy(ILogWriter replace);
	
}
