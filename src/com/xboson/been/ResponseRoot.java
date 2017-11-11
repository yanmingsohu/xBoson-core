////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午2:16:50
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ResponseRoot.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

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
