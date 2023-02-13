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
// 文件创建日期: 17-11-18 上午8:45
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestAuth.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.auth.*;
import com.xboson.auth.impl.LicenseAuthorizationRating;
import com.xboson.db.ConnectConfig;
import com.xboson.db.sql.SqlReader;
import com.xboson.db.SqlResult;
import com.xboson.util.Password;

import java.sql.ResultSet;


public class TestAuth extends Test {

  private ConnectConfig cc;

  public void test() throws Throwable {
    cc = TestDS.connect_config();
    password();
    sql_reader();
    licenseAuth();
  }


  public void licenseAuth() {
    sub("LicenseAuthorizationRating.class");

    PermissionSystem.applyWithApp(
            LicenseAuthorizationRating.class,
            ()-> "api.ide.code.modify.functions()");

    sub("Not pass");

    new Throws(LicenseAuthorizationRating.NoLicense.class) {
      public void run() {
        PermissionSystem.applyWithApp(
                LicenseAuthorizationRating.class,
                ()-> "app.nopass()");
      }
    };
  }


  public void sql_reader() throws Throwable {
    String code = SqlReader.read("login.sql");
    ok(code != null, "read 'login.sql'");

    code = SqlReader.read("login");
    ok(code != null, "read 'login'");

    String[] parmbind = new String[] {"attewfdsafdsaf", "", ""};
    try (SqlResult sr = SqlReader.query("login.sql", cc, parmbind)) {
      ResultSet rs = sr.getResult();
      TestDBMS.show(rs);
    }
  }


  public void password() throws Throwable {
    sub("Encode password");
    // userid
    String userid = "attewfdsafdsaf";
    // password
    String pstrue = "enw8dcnvczkhfd";
    // 加密的 password
    String psword = "D8D1BEB36B1F49238F0FFE376E11E1739570B99F095FEDCA7CC9864C6B358602";
    // 密码修改时间（password_dt）
    String date = "2017-11-18 10:48:32.0";

    String ps = Password.v1(userid, Password.md5lowstr(pstrue), date);
    eq(psword, ps, "encode password");
    msg(ps);
    msg(Password.md5lowstr("111111"));

    beginTime();
    for (int i=0; i<100000; ++i) {
      Password.v1(userid, pstrue, date);
    }
    endTime("100000 count encode password");
  }


  public static void main(String[] a) {
    new TestAuth();
  }


  static public class Who implements IAWho {
    private String id;
    Who(String id) { this.id = id; }
    public String identification() {
      return "/" + id;
    }
    public boolean isRoot() {
      return false;
    }
  }


  static public class Res implements IAResource {
    private String id;
    Res(String id) { this.id = id; }
    @Override
    public String description() {
      return id;
    }
  }


  static public class Where implements IAWhere {
    public boolean apply(IAWho who, IAResource res) {
      return res.description().equals( who.identification() );
    }
  }

}
