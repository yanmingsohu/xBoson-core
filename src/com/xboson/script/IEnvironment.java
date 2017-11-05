/* CatfoOD 2017年11月5日 上午11:40:08 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import javax.script.ScriptException;

/**
 * 每个环境的实例中的数据都是缓存的, 除非创建新的实例, 
 * 否则环境中的变量可能在配置新的沙箱时被修改数据.
 */
public interface IEnvironment {
	
	/**
	 * 设置 box 的环境
	 */
	public void config(Sandbox box) throws ScriptException;
	
}
