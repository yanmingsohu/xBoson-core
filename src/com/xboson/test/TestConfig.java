/* CatfoOD 2017年11月3日 下午12:44:34 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.test;

import java.io.IOException;

import com.xboson.been.Config;
import com.xboson.util.SysConfig;

public class TestConfig extends Test {

	public void test() throws IOException {
		SysConfig sys = SysConfig.getInstance();
		msg( sys.getHomePath() );
		sys.checkConfigFiles();
		
		Config c = sys.readConfig();
		msg(c.configFile);
		msg(c.configPath);
		
		ok(c.sessionPassword.equals("fdsevbvsx_fdsaf"), "bad session ps");
		ok(c.sessionTimeout == 30, "bad session timeout");
		success("config ready");
	}
}
