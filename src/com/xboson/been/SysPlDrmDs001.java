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
// 文件创建日期: 17-11-22 下午1:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/SysPlDrmDs001.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.script.IVisitByScript;

import java.math.BigDecimal;


/**
 * sys_pl_drm_ds001 表映射, 数据库资源管理
 */
public class SysPlDrmDs001 implements IVisitByScript {
  public String did;    // 数据源ID
  public String dn;     // 数据源名称
  public String owner;  // 所有者, 就是 orgid
  public String dbtype; // 数据库类型
  public String cn;     // 数据库中文名称
  public String flg;    // 0 平台, 1 第三方, 9 xBoson 创建
  public String mark;   // 说明
  public String status;

  public String dhost;
  public BigDecimal dport;
  public String url;
  public String user_name;
  public String pass;
  public String en;     // 数据库物理名称
}
