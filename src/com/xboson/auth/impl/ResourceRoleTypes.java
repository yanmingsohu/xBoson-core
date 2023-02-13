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
// 文件创建日期: 17-12-12 下午1:12
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ResourceRoleTypes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.auth.impl;

import com.xboson.auth.IAResource;


/**
 * 资源 id 的枚举, 存储在 redis 中, 用于区分不同资源类型.
 * @see RoleBaseAccessControl
 */
public enum ResourceRoleTypes {
  API("01"),
  MODEL("02"),
  PAGE("04"),
  ELEMENT("05");


  ResourceRoleTypes(String name) {
    this.name       = name;
    this.mid        = ':' + name + ':';
    this.onlyPrefix = ':' + name;
    this.onlySuffix = name + ':';
  }


  public String toString() {
    return mid;
  }


  /**
   * 带有前后 ':' 符号的 name, 适合放在权限的中间.
   */
  public final String mid;

  /**
   * 名称字符串
   */
  public final String name;

  /**
   * 前面带有 ':' 符号的 name
   */
  public final String onlyPrefix;


  /**
   * 后面带有 ':' 符号的 name
   */
  public final String onlySuffix;


  /**
   * 生成角色对该类型资源的访问 key, 该 key 用于访问缓存.
   *
   * @param roleID 角色 id
   * @param resourceDesc 资源描述符
   */
  public String toKEY(String roleID, String resourceDesc) {
    return roleID + mid + resourceDesc;
  }


  /**
   * 生成该类型公共资源的访问 key, 该 key 用于访问缓存.
   *
   * @param resourceDesc 公共资源描述符
   */
  public String toKEY(String resourceDesc) {
    return onlySuffix + resourceDesc;
  }


  /**
   * @see #toKEY(String, String)
   */
  public String toKEY(String roldID, IAResource resource) {
    return toKEY(roldID, resource.description());
  }


  /**
   * @see #toKEY(String)
   */
  public String toKEY(IAResource res) {
    return toKEY(res.description());
  }
}
