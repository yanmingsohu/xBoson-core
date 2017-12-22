////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-14 下午8:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/DefaultConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.Config;
import com.xboson.db.ConnectConfig;
import com.xboson.db.DBPoolConfig;
import com.xboson.script.lib.Uuid;
import com.xboson.test.Test;
import com.xboson.fs.redis.LocalFileMapping;
import redis.clients.jedis.JedisPoolConfig;


public final class DefaultConfig {

  public static final String default_sys_tables = "sys_tenant,sys_tenant_user," +
          "sys_base_tbl,sys_config,sys_server,sys_system,sys_sqls,sys_userinfo," +
          "sys_user_identity,mdm_personal_info,mdm_org,sys_eeb_run_conf," +
          "sys_eeb_work_node,sys_eeb_jobgroup,sys_eeb_varnish,sys_eeb_sche," +
          "sys_eeb_statistics,sys_eeb_detail";

  /**
   * 将配置设置成为默认设置
   */
  public static void setto(Config c) {
    Uuid uuid = new Uuid();

    c.configVersion     = "1.3.1";
    c.loggerWriterType  = "ConsoleOut";
    c.logLevel          = "info";
    c.sessionTimeout    = 24 * 60;
    c.sessionPassword   = Test.randomString(20);
    c.debugService      =  false;
    c.rootUserName      = "root";
    c.rootPassword      = "111111";
    c.rootPid           = uuid.ds();

    c.rootPassword =
            Password.v1(c.rootUserName, Password.md5lowstr(c.rootPassword));

    c.uiProviderClass   = LocalFileMapping.class.getName();
    c.uiUrl             = "/ui";
    c.clusterNodeID     = 0;
    c.sysTableList      = default_sys_tables.split(",");

    JedisPoolConfig j = c.jedispool = new JedisPoolConfig();
    j.setMaxIdle(10);
    j.setMinIdle(0);
    j.setMaxTotal(200);

    DBPoolConfig d = c.dbpool = new DBPoolConfig();
    d.setMaxTotal(2000);
    d.setBlockWhenExhausted(true);
    d.setMaxWaitMillis(3000);
    d.setTestOnBorrow(true);
    d.setTestOnCreate(true);
    d.setTestOnReturn(false);
    d.setTimeBetweenEvictionRunsMillis((long)(1 * 3600e3));
    d.setTestWhileIdle(true);
    d.setNumTestsPerEvictionRun(-1);

    ConnectConfig db = c.db = new ConnectConfig();
    db.setHost("localhost");

    ConnectConfig redis = c.redis = new ConnectConfig();
    redis.setHost("localhost");
    redis.setPort("6379");
    redis.setPassword("");
  }


  private DefaultConfig() {}
}
