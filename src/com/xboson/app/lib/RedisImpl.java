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
// 文件创建日期: 17-12-7 下午5:28
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/RedisImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.app.AppContext;
import com.xboson.auth.impl.ResourceRoleTypes;
import com.xboson.auth.impl.RoleBaseAccessControl;
import com.xboson.been.LoginUser;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.sleep.SafeDataFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 脚本将平台上的很多敏感数据(如数据库密码) 推送到缓存中, 部分敏感数据已经加密
 * @see SafeDataFactory 加密策略
 */
public class RedisImpl implements IApiConstant {

  private final String key_prefix;


  RedisImpl(String prefix) {
    key_prefix = prefix;
  }


  interface IEach {
    void val(int num, String value);
  }


  public void set(String region, String key, String value, int exp) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      SafeDataFactory.IEncryptionStrategy enc = SafeDataFactory.get(tkey);
      client.hset(tkey, enc.encodeKey(key), enc.encodeData(value));
      // 超时会导致整个 region 都失效, 不能这样设置
      // if (exp > 0) client.expire(tkey, exp);
    }
  }


  public String get(String region, String key) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      SafeDataFactory.IEncryptionStrategy enc = SafeDataFactory.get(tkey);
      String sval = client.hget(tkey, enc.encodeKey(key));
      if (sval == null) return null;
      return enc.decodeData(sval);
    }
  }


  public void del(String region, String key) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      SafeDataFactory.IEncryptionStrategy enc = SafeDataFactory.get(tkey);
      client.hdel(tkey, enc.encodeKey(key));
    }
  }


  public void del(String region) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      client.del(tkey);
    }
  }


  public List<Object> delAll(String region, String[] keys) throws IOException {
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      SafeDataFactory.IEncryptionStrategy enc = SafeDataFactory.get(tkey);

      List<Object> ret = new ArrayList<>();
      for (int i=0; i<keys.length; ++i) {
        ret.add( client.hdel(tkey, enc.encodeKey(keys[i])) );
      }
      return ret;
    }
  }


  public Object keys(ScriptObjectMirror list, String region) {
    int size = list.size();
    keys(region, (i, val)->{
      list.setSlot(size + i, val);
    });
    return list;
  }


  public int keys(String region, IEach call) {
    int i = 0;
    try (IRedis client = RedisMesmerizer.me().open()) {
      String tkey = key_prefix + region;
      SafeDataFactory.IEncryptionStrategy enc = SafeDataFactory.get(tkey);
      Iterator<String> it = client.hkeys(tkey).iterator();

      while (it.hasNext()) {
        call.val(i, enc.decodeKey(it.next()));
        ++i;
      }
    }
    return i;
  }


  /**
   * 模糊查询不支持key加密
   */
  public int keys(String region, String pattern, IEach call) {
    String tkey = key_prefix + region;

    SafeDataFactory.IEncryptionStrategy s = SafeDataFactory.getMaybeNull(tkey);
    if (s != null && !s.keyAmbiguous()) throw new UnsupportedOperationException(
              "The region '"+ region +"' is encrypted and cannot be ambiguous");

    String cursor = RedisMesmerizer.BEGIN_OVER_CURSOR;
    ScanParams sp = new ScanParams();
    sp.match(pattern);
    int i = 0;

    try (IRedis client = RedisMesmerizer.me().open()) {
      for (;;) {
        ScanResult<Map.Entry<String, String>> sr = client.hscan(tkey, cursor, sp);
        Iterator<Map.Entry<String, String>> it = sr.getResult().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> item = it.next();
          call.val(i, item.getKey());
          ++i;
        }

        cursor = sr.getStringCursor();
        if (cursor.equals(RedisMesmerizer.BEGIN_OVER_CURSOR))
          break;
      }
    }
    return i;
  }


  public Object keys(ScriptObjectMirror list, String region, String pattern) {
    int count = list.size();
    keys(region, pattern, (i, v)-> {
      list.setSlot(count + i, v);
    });
    return list;
  }


  /**
   * 返回当前用户角色, 对资源类型的访问权限数据
   *
   * @param type 资源类型
   * @param resourceId 资源的主键
   * @return 在 redis 中存储的值, 可能是 "0"
   */
  public Object getRoleInfo(ResourceRoleTypes type, String resourceId) {
    LoginUser user = (LoginUser) AppContext.me().who();
    return RoleBaseAccessControl.check(user, type, resourceId, false);
  }
}
