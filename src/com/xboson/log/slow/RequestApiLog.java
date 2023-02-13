/**
 *  Copyright 2023 Jing Yanming
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
////////////////////////////////////////////////////////////////////////////////
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
import com.xboson.db.IDict;
import com.xboson.db.sql.SqlReader;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.c0nst.IConstant;


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


  public void log(ApiCall ac, long elapsed, Throwable err) {
    insert( uuid.ds(),
            nowInternet(),
            err == null ? IDict.ERR_TYPE_NONE : IDict.ERR_TYPE_API,
            ac.exparam.get(IConstant.REQUEST_ID),
            cut(ac.call.req.getRequestURI(), err, 2000),
            ac.org,
            ac.call.sess.login_user.pid,
            ac.call.getRemoteAddr(),
            ac.app,
            ac.mod,
            ac.api,
            elapsed,
            cut(ac.call.req.getHeader(HEADER_REFERRER), null, 200),
            cut(ac.call.req.getHeader(HEADER_USER_AGENT), null, 100) );
  }


  private String cut(String str, Throwable err, int max) {
    StringBuilder buf = new StringBuilder();
    if (err != null) {
      buf.append(err.getMessage());
      buf.append(", \n");
    }
    if (str != null) {
      buf.append(str);
    }
    if (buf.length() > max) {
      return buf.substring(0, max);
    }
    return buf.toString();
  }


  @Override
  public String logName() {
    return "log-req-api";
  }
}
