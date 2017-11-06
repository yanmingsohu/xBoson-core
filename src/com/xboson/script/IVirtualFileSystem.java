/* CatfoOD 2017年11月5日 下午2:32:09 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.script;

import java.io.IOException;

/**
 * 每个机构的每个应用都有一个虚拟文件系统 
 */
public interface IVirtualFileSystem {

	/**
	 * 读取路径上的文件, 返回文件内容, 如果文件不存在应该抛出异常
	 */
	public String readFile(String path) throws IOException;
	
	
	/**
	 * 返回文件系统的id, 不同机构的id不同
	 */
	public String getID();
	
}
