////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 20-5-22 下午8:21
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/RedisConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.util.Tool;


public class RedisConfig extends JsonHelper {

  String dbname;
  Integer dbid;
  String host;
  String port;
  String database;
  String username;
  String password;
  boolean cluster;


  public String getDbname() {
    return dbname;
  }


  public void setDbname(String dbname) {
    this.dbname = dbname;
  }


  public Integer getDbid() {
    return dbid;
  }


  public void setDbid(Integer dbid) {
    this.dbid = dbid;
  }


  public String getHost(String defaultHost) {
    if (Tool.isNulStr(host)) {
      return defaultHost;
    }
    return host;
  }


  public String getHost() {
    return host;
  }


  public void setHost(String host) {
    this.host = host;
  }


  public int getIntPort(int defaultValue) {
    try {
      return Integer.parseInt(port);
    } catch(Exception e) {
      return defaultValue;
    }
  }


  public String getPort() {
    return port;
  }


  public void setPort(String port) {
    this.port = port;
  }


  public String getDatabase() {
    return database;
  }


  public void setDatabase(String database) {
    this.database = database;
  }


  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }


  public String getPassword(String defaultPs) {
    if (Tool.isNulStr(password)) {
      return defaultPs;
    }
    return password;
  }


  public void setPassword(String password) {
    this.password = password;
  }


  public boolean isCluster() {
    return cluster;
  }


  public void setCluster(boolean cluster) {
    this.cluster = cluster;
  }

}
