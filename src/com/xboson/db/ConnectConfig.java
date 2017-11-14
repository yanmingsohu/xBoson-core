////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-11-14 上午10:48
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/ConnectConfig.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

import com.xboson.been.JsonHelper;


/**
 * 该类的经过专门设计, 只为连接池判断是否是一个连接而提供,
 * equals() 并不是每个字段判断相等,
 * hashCode() 仅在特殊时期有效.
 */
public final class ConnectConfig extends JsonHelper {

  /**
   * 数据库类型
   * @see IDriver#name()
   */
  String dbname;

  /**
   * 数据库类型 id
   * @see IDriver#id()
   */
  Integer dbid;

  String host;
  String port;

  /**
   * 不同的 dbms 对 '数据库' 的定义不同, 有时叫 scheme 或 SID
   */
  String database;

  String username;
  String password;


  public String getDbname() {
    return dbname;
  }


  public void setDbname(String dbname) {
    __hashcode = 0;
    this.dbname = dbname;
  }


  public String getHost() {
    return host;
  }


  public void setHost(String host) {
    __hashcode = 0;
    this.host = host;
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
    __hashcode = 0;
    this.username = username;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(String password) {
    this.password = password;
  }


  private void __computer_hashcode() {
    __hashcode = ('/'+ host +'/'+ dbname +'/'+ username).hashCode();
  }


  public Integer getDbid() {
    return dbid;
  }


  public void setDbid(int dbid) {
    this.dbid = dbid;
  }


  @Override
  public boolean equals(Object o) {
    if (o instanceof ConnectConfig) {
      if (o == this) return true;

      ConnectConfig cc = (ConnectConfig) o;
      return host != null
              && host.equals(cc.host)
              && dbname != null
              && dbname.equals(cc.dbname)
              && username != null
              && username.equals(cc.username);
    }
    return false;
  }


  @Override
  public int hashCode() {
    if (__hashcode == 0)
      __computer_hashcode();

    return __hashcode;
  }

  private int __hashcode = 0;


  public String toString() {
    return "["+ host +':'+ port
            +'@'+ username
            +'/'+ dbname +'/'+ database
            +"]";
  }
}
