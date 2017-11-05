/* CatfoOD 2017年11月5日 下午4:27:04 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.io.IOException;

import javax.script.ScriptException;

import com.xboson.been.Module;


public interface ICodeRunnner {

	/**
	 * 通过该方法运行代码
	 */
	public Module run(String path) throws IOException, ScriptException;
	
}
