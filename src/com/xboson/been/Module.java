/* CatfoOD 2017年11月5日 下午1:53:54 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import com.xboson.util.Tool;

public class Module {
	public String 	id;
	public String 	filename;
	public Object   children;
	public Object 	exports;
	public boolean 	loaded;
	public Object 	parent;
	public String[] paths;
	
	
	public Module() {
		loaded = false;
	}
	
	
	public String toString() {
		return Tool.getAdapter(Module.class).toJson(this);
	}
}
