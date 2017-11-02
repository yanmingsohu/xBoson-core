/* CatfoOD 2017年11月2日 下午2:16:50 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.been;

import com.xboson.util.Tool;


public class ResponseRoot {

	private int code;
	private String msg;
	private Object data;
	private String datatype;
	
	
	public ResponseRoot() {
		code = 0;
		msg  = null;
		data = null;
	}


	
	public ResponseRoot(Throwable e) {
		data = Tool.miniStack(e, 5);
		datatype = "ErrorStack";
		code = 500;
		msg  = e.getMessage();
	}
	


	public int getCode() {
		return code;
	}


	
	public void setCode(int code) {
		this.code = code;
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


	public void setData(Object data) {
		this.data = data;
		this.datatype = data.getClass().getName();
	}

	
	public String getDatatype() {
		return datatype;
	}

	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
}
