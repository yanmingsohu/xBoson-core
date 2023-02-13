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
// 文件创建日期: 19-1-10 上午11:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Firewall.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;


public class Firewall {

  public static final int IP_BAN_COUNT    = 10;
  public static final int IP_WAIT_TIMEOUT = 10; // 分钟
  public static final int IP_NEED_CAPTCHA = 5;

  private static Firewall instanse;

  private RedisMesmerizer redis;


  private Firewall() {
    redis = RedisMesmerizer.me();
  }


  public static Firewall me() {
    if (instanse == null) {
      synchronized (Firewall.class) {
        if (instanse == null) {
          instanse = new Firewall();
        }
      }
    }
    return instanse;
  }


  /**
   * ip 登录失败次数超限返回 true
   */
  public int checkIpBan(String ip) {
    try (IRedis client = redis.open()) {
      String key = "/ip-ban/" + ip;
      String v = client.get(key);
      if (v == null) return 0;
      return Integer.parseInt(v);
    }
  }


  /**
   * 记录一次 ip 失败登录次数
   */
  public void ipLoginFail(String ip) {
    try (IRedis client = redis.open()) {
      String key = "/ip-ban/" + ip;
      client.incr(key);
      client.expire(key, IP_WAIT_TIMEOUT * 60);
    }
  }
}
