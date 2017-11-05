/* CatfoOD 2017年11月5日 上午11:47:37 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;


/**
 * 默认总是冻结的且不可变变量值.
 */
public abstract class JSObject implements IJSObject {

	@Override
	public boolean freeze() {
		return true;
	}

	@Override
	public void init() {
	}

	@Override
	public void destory() {
	}

	@Override
	public String env_name() {
		return null;
	}

}
