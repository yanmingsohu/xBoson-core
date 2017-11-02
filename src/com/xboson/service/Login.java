/* CatfoOD 2017年11月2日 下午3:33:52 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.service;

import java.io.IOException;

import javax.servlet.ServletException;

import com.xboson.been.CallData;
import com.xboson.j2ee.container.XService;


public class Login extends XService {

	@Override
	public int service(CallData data) throws ServletException, IOException {
		data.json.response("Auth work but not implement.");
		return 0;
	}

}
