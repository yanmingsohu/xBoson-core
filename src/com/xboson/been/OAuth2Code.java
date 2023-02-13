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
// 文件创建日期: 18-3-13 下午2:59
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/OAuth2Code.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.service.OAuth2;
import com.xboson.sleep.IBinData;
import com.xboson.sleep.ITimeout;


public class OAuth2Code implements IBinData, ITimeout {

  public long begin;
  public String code;
  public String userid;
  public String clientid;


  public OAuth2Code(String code) {
    this.code = code;
  }


  public OAuth2Code() {}


  @Override
  public String getid() {
    return code;
  }


  @Override
  public boolean isTimeout() {
    return begin + OAuth2.CODE_LIFE < System.currentTimeMillis();
  }
}
