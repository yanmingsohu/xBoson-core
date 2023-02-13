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
// 文件创建日期: 17-12-26 下午1:14
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/impl/InvocationApi.java
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
import com.xboson.been.LoginUser;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.JavaConverter;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


public class InvocationApi implements IAWhere {

  /** 不做权限检查的接口集合, 内容为资源描述字符串 */
  private Set<String> skip_check;
  private Log log;


  public InvocationApi() {
    this.log = LogFactory.create();
    this.skip_check = createSkip();
  }


  public String toString() {
    return "执行接口";
  }


  private Set<String> createSkip() {
    return JavaConverter.arr2set(new String[]{
            "zyapp_loginzymodule_logingetuserorg",
    });
  }


  @Override
  public boolean apply(IAWho who, IAResource res) {
    if (skip_check.contains(res.description()))
      return true;

    LoginUser user = (LoginUser) who;
    String auth = RoleBaseAccessControl.check(
            user, ResourceRoleTypes.API, res, true);

    if (auth == null) throw new ApiPermission(user, res);
    return true;
  }


  private class ApiPermission extends PermissionException {
    public ApiPermission(LoginUser user, IAResource res) {
      super(user, InvocationApi.this, res, 1101);

      log.debug("No api auth", user.userid,
              "Roles:", user.roles, "Api Description:", res.description());
    }
  }
}
