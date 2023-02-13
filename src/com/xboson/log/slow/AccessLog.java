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
// 文件创建日期: 18-1-23 上午9:55
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/slow/AccessLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.slow;

import com.xboson.been.LoginUser;
import com.xboson.db.sql.SqlReader;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;


/**
 * 记录用户登录时的日志
 */
public class AccessLog extends AbsSlowLog {

  public static final String SQL_FILE = "log_access.sql";


  public AccessLog() {
  }


  @Override
  protected String getSql() {
    return SqlReader.read(SQL_FILE);
  }


  /**
   * 记录用户登录日志
   * @param user 登录用户
   * @param state 字典 ZR.0023 (<9999)
   * @param message 自定义消息, 可以 null
   */
  public void log(LoginUser user, int state, String message) {
    log(user.pid, state, message);
  }


  public void log(String user_pid, int state, String message) {
    insert( uuid.ds(),
            nowInternet(),
            message,
            user_pid,
            Integer.toString(state) );
  }


  @Override
  public String logName() {
    return "log-access";
  }
}
