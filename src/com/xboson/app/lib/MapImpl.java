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
// 文件创建日期: 17-11-23 上午11:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/MapImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.script.IJSObject;

import java.util.Collections;
import java.util.WeakHashMap;


public class MapImpl extends RuntimeUnitImpl implements IJSObject {


  public MapImpl() {
    super(null);
  }


  public Object syncMap() {
    return new WeakHashMap();
  }


  public Object weakSyncMap() {
    return Collections.synchronizedMap(new WeakHashMap());
  }


  @Override
  public String env_name() {
    return "map";
  }


  @Override
  public boolean freeze() {
    return true;
  }


  @Override
  public void init() {
  }


  @Override
  public void destory() {
  }


  public Object get(Object map, String key) {
    return wrap(map).getMember(key);
  }


  public Object put(Object map, String k, Object v) {
    wrap(map).setMember(k, v);
    return map;
  }


  public Object putAll(Object tar, Object src) {
    wrap(tar).putAll(wrap(src));
    return tar;
  }


  public Object remove(Object map, Object key) {
    wrap(map).remove(key);
    return map;
  }


  public boolean containsKey(Object map, String key) {
    return wrap(map).hasMember(key);
  }


  public int size(Object map) {
    return wrap(map).size();
  }

}
