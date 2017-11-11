////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午3:42:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/CallData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xboson.util.JsonResponse;

/**
 * 全部属性公共不可变方法
 */
public class CallData {

	public final HttpServletRequest req;
	public final HttpServletResponse resp; 
	public final UrlSplit url;
	public final JsonResponse json;
	public final SessionData sess;
	public final ResponseRoot ret;
	
	
	public CallData(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		this.req  = req;
		this.resp = resp;
		this.json = JsonResponse.get(req);
		this.sess = SessionData.get(req);
		this.url  = new UrlSplit(req);
		this.ret  = json.getRoot();
	}
}
