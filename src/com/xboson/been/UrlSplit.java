////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月2日 下午3:00:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/UrlSplit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import javax.servlet.http.HttpServletRequest;

/**
 * 将请求 url 分解, 用于路由映射
 * 当入参为 /a/b/c
 * 	 name = /a
 *   last = /b/c
 */
public class UrlSplit implements IBean {

  private static final String DEFAULT_ERR = "cannot split null string url.";
	
	/** url 中的首个路径 */
	private String name;
	/** url 中的后续路径 */
	private String last;


	public UrlSplit(HttpServletRequest req) {
		String cp = req.getContextPath();
		String rq = req.getRequestURI();
		split( rq.substring(cp.length(), rq.length()), DEFAULT_ERR );
	}
	
	
	public UrlSplit(String s, String errmsg) {
		split(s, errmsg);
	}


	public UrlSplit(String s) {
	  this(s, DEFAULT_ERR);
  }


	/**
	 * 这会改变自身的数据, 并返回 name
	 */
	public String split(String s, String errMessage) {
		if (s == null) 
			throw new RuntimeException(errMessage);

		try {
      int a = 0;
      if (s.charAt(0) == '/') a = 1;
      a = s.indexOf('/', a);

      if (a >= 0) {
        name = s.substring(0, a);
        last = s.substring(a, s.length());
      } else {
        name = s;
        last = null;
      }
      return name;
    } catch(Exception e) {
		  throw new RuntimeException(errMessage, e);
    }
	}


	public String next(String errMessage) {
	  return split(last, errMessage);
  }
	
	
	/**
	 * 将 last 再次拆分
	 */
	public UrlSplit sub() {
		return new UrlSplit(last, DEFAULT_ERR);
	}


	public UrlSplit sub(String errmsg) {
	  return new UrlSplit(last, errmsg);
  }
	
	
	public String toString() {
		return name + " : " + last;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getLast() {
		return last;
	}
}
