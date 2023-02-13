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
// 文件创建日期: 17-11-23 上午11:22
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/CacheImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.CallData;
import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;


/**
 * 每个请求一个实例
 */
public class CacheImpl extends RuntimeUnitImpl {

  private RedisImpl redis;


  public CacheImpl(CallData cd, String orgid) {
    super(cd);
    redis = new RedisImpl("/user_space/" + orgid + '/');
  }


  public void set(String region, String key, Object value) {
    set(region, key, value, IConstant.DEFAULT_TIMEOUT);
  }


  public void set(String region, String key, Object value, int exp) {
    String str = jsonStringify(value);
    redis.set(region, key, str, exp);
  }


  public Object get(String region, String key) {
    String s = redis.get(region, key);
    return jsonParse(s);
  }


  public Object del(String region, String key) {
    redis.del(region, key);
    return key;
  }


  public Object delAll(String region, String[] keys) throws IOException {
    return redis.delAll(region, keys);
  }


  public Object keys(String region) {
    return redis.keys(createJSList(), region);
  }


  public int keys(String region, ScriptObjectMirror callback) {
    if (! callback.isFunction()) {
      throw new XBosonException.BadParameter("callback",
              "must be Function(index, key)");
    }
    return redis.keys(region, (i, k)->{
      callback.call(null, i, k);
    });
  }
}
