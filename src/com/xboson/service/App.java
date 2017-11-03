/* CatfoOD 2017年11月3日 上午10:38:36 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.service;

import java.io.IOException;

import javax.servlet.ServletException;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XPath;
import com.xboson.j2ee.container.XService;


@XPath("/app")
public class App extends XService {

	@Override
	public int service(CallData data) throws ServletException, IOException {
		return 0;
	}

}
