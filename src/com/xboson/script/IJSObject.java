/* CatfoOD 2017年11月5日 上午11:40:55 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;


public interface IJSObject {

	/**
	 * 返回对象在环境中的变量名称
	 */
	public String env_name();
	
	/**
	 * 返回对象中的属性是否应该被冻结
	 */
	public boolean freeze();
	
	/**
	 * 当环境被创建时, 初始化自身, 该方法被环境调用
	 */
	public void init();
	
	/**
	 * 当环境被销毁时, 调用
	 */
	public void destory();
	
}
