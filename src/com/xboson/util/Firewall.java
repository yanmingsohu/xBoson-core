////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-1-10 上午11:08
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/Firewall.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import com.xboson.sleep.RedisMesmerizer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;


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
    try (Jedis client = redis.open()) {
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
    try (Jedis client = redis.open();
         Transaction t = client.multi() ) {
      String key = "/ip-ban/" + ip;
      t.incr(key);
      t.expire(key, IP_WAIT_TIMEOUT * 60);
      t.exec();
    } catch (IOException e) {
      throw new XBosonException(e);
    }
  }
}
