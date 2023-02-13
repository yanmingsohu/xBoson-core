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
// 文件创建日期: 19-9-18 下午2:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/slow/LoginLog.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log.slow;

import com.xboson.been.CallData;
import com.xboson.been.LoginUser;
import com.xboson.db.sql.SqlReader;

import java.sql.Date;


/**
 * 登录后更新用户表中的 ip 字段
 */
public class LoginLog extends AbsSlowLog {

  public static final String SQL_FILE = "log_userlogin.sql";


  @Override
  protected String getSql() {
    return SqlReader.read(SQL_FILE);
  }


  @Override
  public String logName() {
    return "log-user-login";
  }


  public void login(LoginUser user, CallData cd) {
    insert(cd.getRemoteAddr(), user.pid);
  }
}
