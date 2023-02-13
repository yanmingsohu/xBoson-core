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
// 文件创建日期: 17-12-18 下午4:49
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/LuaScript.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import com.xboson.been.XBosonException;
import com.xboson.util.CodeFormater;
import com.xboson.util.JavaConverter;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;
import redis.clients.jedis.exceptions.JedisNoScriptException;


/**
 * 在 Redis 中运行的 Lua 脚本, 线程安全
 */
public class LuaScript {

  private final static String[] NUL_PARA = new String[0];

  private String luaScript;
  private String hash;
  private String key;


  private LuaScript(String src, String key) {
    this.luaScript = src;
    this.hash = null;
    this.key = key;
  }


  private void _complie(IRedis client) {
    hash = client.scriptLoad(luaScript, key);
  }


  /**
   * 编译一个脚本
   * @param luaScript 脚本代码
   * @param key 在集群时, 影响运行所在的节点
   * @return
   */
  public static LuaScript compile(String luaScript, String key) {
    return new LuaScript(luaScript, key);
  }


  /**
   * @see #compile(String, String)
   */
  public static LuaScript compile(StringBufferOutputStream buf, String key) {
    return compile(buf.toString(), key);
  }


  /**
   * 运行脚本, 无参数
   */
  public Object eval() {
    return eval(0);
  }


  /**
   * 运行脚本,
   *    key 在脚本中用 KEYS[1~ ] 引用
   *    参数在脚本中使用 ARGV[1~ ] 引用
   *
   * @param keyCount key 在参数中的数量
   * @param parameters 包含 key 和参数
   * @return 结果集
   */
  public Object eval(int keyCount, Object... parameters) {
    try (IRedis client = RedisMesmerizer.me().open()) {
      if (hash == null || client.scriptExists(hash, key)) {
        _complie(client);
      }

      int retry = 2;
      String[] ps = JavaConverter.toStringArr(parameters, NUL_PARA);

      do {
        try {
          return client.evalsha(hash, keyCount, ps);
        } catch (JedisNoScriptException e) {
          _complie(client);
        }
      } while (--retry > 0);

      throw new XBosonException("Cannot eval lua");
    }
  }


  @Override
  public String toString() {
    return luaScript;
  }
}
