////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2018 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 18-1-23 上午11:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/slow/RequestApiLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.slow;

import com.xboson.been.ApiCall;
import com.xboson.db.sql.SqlReader;
import com.xboson.util.IConstant;


public class RequestApiLog extends AbsSlowLog {

  public static final String SQL_FILE = "log_api_request.sql";
  public static final String HEADER_REFERRER = "referer";
  public static final String HEADER_USER_AGENT = "user-agent";


  public RequestApiLog() {
  }


  @Override
  protected String getSql() {
    return SqlReader.read(SQL_FILE);
  }


  public void log(ApiCall ac) {
    insert( uuid.ds(),
            nowInternet(),
            ac.exparam.get(IConstant.REQUEST_ID),
            max(ac.call.req.getRequestURI(), 2000),
            ac.org,
            ac.call.sess.login_user.pid,
            ac.call.req.getRemoteAddr(),
            ac.app,
            ac.mod,
            ac.api,
            max(ac.call.req.getHeader(HEADER_REFERRER), 200),
            max(ac.call.req.getHeader(HEADER_USER_AGENT), 100) );
  }


  private String max(String str, int max) {
    if (str == null) return null;
    if (str.length() > max) {
      return str.substring(0, max);
    }
    return str;
  }

}
