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
// 文件创建日期: 17-11-22 下午1:32
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/db/IDict.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.db;

/**
 * 提供了常用字典的常量, 直接继承该接口
 */
public interface IDict {

  String ZR001_ENABLE = "1";
  String ZR001_DISABLE = "0";


  /** SYS08.00.006.00 */
  String ADMIN_FLAG_USER = "0";
  String ADMIN_FLAG_ADMIN = "1";
  String ADMIN_FLAG_TENANT_ADMIN = "2";


  int JOB_UNIT_YEAR   = 30;
  int JOB_UNIT_MONTH  = 31;
  int JOB_UNIT_WEEK   = 40;
  int JOB_UNIT_DAY    = 50;
  int JOB_UNIT_DAY2   = 60;
  int JOB_UNIT_HOUR   = 70;
  int JOB_UNIT_SECOND = 90;
  int JOB_UNIT_MINUTE = 80;


  /** 初始化, 尚未运行过 */
  int JOB_STATUS_INIT     = 0;
  /** 运行的任务正在请求 api 但未返回 */
  int JOB_STATUS_RUNNING  = 1;
  /** 运行的任务休眠中 */
  int JOB_STATUS_STOP     = 3;
  /** 系统错误, 网络不通等 */
  int JOB_STATUS_ERR      = 2;
  /** 达到结束时间 */
  int JOB_STATUS_TIMEUP   = 4;
  /** 达到运行次数 */
  int JOB_STATUS_MAXCOUNT = 5;
  /** api 返回了一些东西 */
  int JOB_STATUS_LOG      = 6;
  /** 任务已经删除 */
  int JOB_STATUS_DEL      = 7;

  /** 异常类型(ZR.0024) */
  String ERR_TYPE_API     = "API";
  String ERR_TYPE_NONE    = "NONE";
}
