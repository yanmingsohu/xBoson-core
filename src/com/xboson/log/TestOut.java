/* CatfoOD 2017年11月5日 下午1:38:59 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.log;

import java.util.Date;

import com.xboson.test.Test;

public class TestOut extends OutBase implements ILogWriter {

	@Override
	public void output(Date d, Level l, String name, Object[] msg) {
		StringBuilder buf = new StringBuilder();
		format(buf, d, l, name, msg);
		Test.msg(buf.toString());
	}

	@Override
	public void destroy(ILogWriter replace) {
	}

}
