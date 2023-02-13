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
// 文件创建日期: 17-12-15 下午5:01
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/ApiPath.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.ApiCall;

import java.util.Map;


/**
 * 统一所有关于 api 生成抽象路径的算法, 保证同样参数生成的路径相同;
 * 路径中已经含有文件系统前缀.
 */
public class ApiPath {


  /** 核心 */
  public static String getPath(String org, String app, String mod, String api) {
    return "/" + org + "/" + app + "/" + mod + "/" + api;
  }


  public static String getPath(ApiCall ac) {
    return getPath(ac.org, ac.app, ac.mod, ac.api);
  }


  public static String getPath(Map<String, Object> map, String api) {
    return getPath((String) map.get("org"),
            (String) map.get("app"), (String) map.get("mod"), api);
  }


  public static String getModPath(Map<String, Object> map) {
    return "/"+ map.get("org") +'/'+ map.get("app") +'/'+ map.get("mod");
  }


  public static String getAppPath(Map<String, Object> map) {
    return "/"+ map.get("org") +'/'+ map.get("app");
  }


  public static String toFile(String module_id, String api_id) {
    return '/' + module_id + '/' + api_id;
  }


  public static String getPathOrgFromContext(String app, String mod, String api) {
    return getPath(AppContext.me().originalOrg(), app, mod, api);
  }


  /**
   * 返回 api 脚本修改事件的消息名称
   * type - 消息类型前缀
   * filename
   */
  public static String getEventPath(String type, String filename) {
    return type + filename.toLowerCase();
  }


  public static String getEventPath(String filename) {
    ApiTypes type = AppContext.me().getApiModeType();
    return getEventPath(type.eventPrifix, filename);
  }


  public static String getEventPath() {
    AppContext ac = AppContext.me();
    return getEventPath(ac.getApiModeType(), ac.getCurrentApiPath());
  }


  public static String getEventPath(ApiTypes type, String filename) {
    return getEventPath(type.eventPrifix, filename);
  }
}
