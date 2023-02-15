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
// 文件创建日期: 18-4-3 上午7:23
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/impl/LicenseAuthorizationRating.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth.impl;

import com.xboson.auth.IAResource;
import com.xboson.auth.IAWhere;
import com.xboson.auth.IAWho;
import com.xboson.auth.PermissionException;
import com.xboson.been.License;
import com.xboson.j2ee.container.Processes;
import com.xboson.j2ee.container.Striker;


/**
 * 检查 License 并决定资源是否能访问, 一定不会检查 IAWho 参数.
 */
public class LicenseAuthorizationRating implements IAWhere {

  private Processes plc;


  public LicenseAuthorizationRating() {
    plc = Processes.me();
  }


  @Override
  public boolean apply(IAWho who, IAResource res) {
    // 开源版没有任何限制, 注释这一行则不进行授权检查
    // checkLicense(res);
    return true;
  }


  private void checkLicense(IAResource res) {
    String msg = plc.message();

    if (msg != null) {
      throw new NoLicense(msg);
    }

    String api = res.description();
    License lic = plc.message2();

    if (lic.api == null || (! lic.api.contains(api))) {
      throw new NoLicense(api);
    }
  }


  @Override
  public boolean passAdmin() {
    return false;
  }


  public String toString() {
    return "接口授权检查";
  }


  public class NoLicense extends PermissionException {
    private NoLicense(String msg) {
      super(Processes.s[8] + msg);
    }
  }
}
