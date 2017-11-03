/* CatfoOD 2017年11月2日 下午2:16:50 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import com.xboson.util.Tool;


/**
 * 平台应答数据根节点, 数据将被转换为 json
 */
public class ResponseRoot {

	/** 为兼容 v1 平台而设置 */
	@SuppressWarnings("unused")
	private int ret;
	
	private int code;
	private String msg;
	private Object data;
	private String datatype;
	
	
	public ResponseRoot() {
		code = 0;
		ret  = 0;
		msg  = null;
		data = null;
	}


	
	public ResponseRoot(Throwable e) {
		setError(e);
	}
	


	public int getCode() {
		return code;
	}


	
	public void setCode(int code) {
		this.code = code;
		this.ret  = code;
	}


	
	public String getMsg() {
		return msg;
	}


	
	public void setMsg(String msg) {
		this.msg = msg;
	}


	
	public Object getData() {
		return data;
	}


	public void setData(Object data, boolean usedataWithType) {
		this.data = data;
		if (usedataWithType) {
			setDatatype(data.getClass().getName());
		}
	}
	
	
	public void setData(Object data) {
		setData(data, true);
	}
	
	
	public void setError(Throwable e) {
		data = Tool.miniStack(e, 5);
		datatype = "ErrorStack";
		code = 500;
		ret  = 500;
		msg  = e.getMessage();
	}

	
	public String getDatatype() {
		return datatype;
	}

	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
}
