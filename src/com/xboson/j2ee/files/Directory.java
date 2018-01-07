////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-26 下午2:36
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/files/Directory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.files;

import com.xboson.been.CallData;
import com.xboson.been.SessionData;
import com.xboson.been.UrlSplit;
import com.xboson.util.Tool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


/**
 * 路径生成规则:
 *    当用户登录一级目录为用户 id, 否则为 temporary;
 *    二级目录为 servlet 服务路径之后的路径字符串, 使用 '/' 拼接
 */
public class Directory {

  public static String get(HttpServletRequest req) throws ServletException {
    SessionData sess = SessionData.get(req);
    UrlSplit sp = new UrlSplit(req);
    return get(sess, sp, req.getParameter("org"));
  }


  public static String get(CallData cd) {
    return get(cd.sess, cd.url, cd.req.getParameter("org"));
  }


  public static String get(SessionData sess, UrlSplit sp, String org) {
    String dirname;

    if (org != null) {
      dirname = "/" + org;
    }
    else if (sess == null || sess.login_user == null) {
      dirname = "/temporary";
    }
    else {
      dirname = "/" + sess.login_user.userid;
    }

    if (sp.getLast() != null) {
      dirname = Tool.normalize(dirname + sp.getLast());
    }

    return dirname;
  }
}
