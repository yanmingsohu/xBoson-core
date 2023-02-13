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
// 文件创建日期: 17-11-30 上午10:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/auth/impl/OpenSystemDBWithKey.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth.impl;

import com.xboson.auth.IAResource;
import com.xboson.auth.IAWhere;
import com.xboson.auth.IAWho;
import com.xboson.been.XBosonException;

import java.net.URI;
import java.net.URISyntaxException;


public class OpenSystemDBWithKey implements IAWhere {

  @Override
  public boolean apply(IAWho who, IAResource res) {
    return false;
  }


  public static IAResource wrap(String key) {
    return new IAResource() {
      @Override
      public String description() {
        return key;
      }
    };
  }
}
