/* CatfoOD 2017年11月2日 下午6:00:58 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

/**
 * 日志输出
 */
public interface ILogWriter {

	void output(Date d, Level l, String name, Object[] msg);
	
	void destroy();
}
