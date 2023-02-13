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
