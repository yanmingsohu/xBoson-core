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
// 文件创建日期: 17-12-8 上午8:56
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/IApiConstant.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.util.c0nst.IConstant;


public interface IApiConstant {

  /** 所有通过 se 对象操作 redis 都会添加这个前缀 */
  String _R_KEY_PREFIX_                 = "/sys";

  String _CACHE_REGION_API_             = "/api";
  String _CACHE_REGION_SYS_SQL_         = "/sql";
  String _CACHE_REGION_SYS_AUTHORITY_   = "/auth";
  String _CACHE_REGION_RBAC_            = "/rbac";
  String _CACHE_REGION_PAGE_            = "/page";
  String _CACHE_REGION_LOGON_           = "/logon";
  String _CACHE_REGION_MDM_             = "/mdm";
  String _CACHE_REGION_TP_APP_          = "/app";
  String _CACHE_REGION_CONFIG_          = "/config";
  String _CACHE_REGION_JDBC_CONNECTION_ = "/jdbc";
  String _CACHE_REGION_TENANT_          = "/tenant";
  String _CACHE_REGION_SYS_CONFIG_      = "/sys-config";
  String _CACHE_REGION_SCHEDULE_        = "/sche";
  String _CACHE_REGION_BIZ_MODEL_       = "/biz-model";
  String _CACHE_REGION_DATASET_         = "/dataset";
  String _CACHE_REGION_SYSTEM_          = "/system";

  String _CACHE_KEY_READY_              = "/ready";
  String _CACHE_KEY_INIT_ORG_V_         = "/org-v";
  String _CACHE_KEY_INIT_ORG_           = "/org";

  String _ORGID_PLATFORM_               = IConstant.SYS_ORG;
  String _COUNT_SUFFIX_                 = "_count";
  String _COUNT_NAME_                   = "count";

}
