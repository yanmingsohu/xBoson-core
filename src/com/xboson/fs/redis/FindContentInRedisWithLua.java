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
// 文件创建日期: 17-12-18 下午5:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/ui/FindContentInRedisWithLua.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.fs.redis;

import com.xboson.sleep.IRedis;
import com.xboson.sleep.LuaScript;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.StringBufferOutputStream;
import com.xboson.util.Tool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;


/**
 * 在 redis 中检索所有文件, 寻找指定的文本内容.
 * 线程安全的, 创建一个即可, 带有缓存功能.
 */
public class FindContentInRedisWithLua {

  /** lua 脚本相对本类路径 */
  public final static String LUA_SCRIPT_PATH = "find_text.lua";
  /** 默认搜索开始路径 */
  public final static String DEF_BASE = IRedisFileSystemProvider.ROOT;
  /** 缓存最多数量, 超过则清空缓存 */
  public final static int MAX_CACHE_C = 1000;
  /** 结果集最大数量, 超过后的数据被忽略 */
  public final static int MAX_RESULT_COUNT = IRedisFileSystemProvider.MAX_RESULT_COUNT;

  private final LuaScript script;
  private final Map<String, FinderResult> cache;
  private final String contentName;
  private final String structName;


  /**
   * 设置搜索目录
   */
  public FindContentInRedisWithLua(IFileSystemConfig config) {
    StringBufferOutputStream buf =
            Tool.readFileFromResource(RedisBase.class, LUA_SCRIPT_PATH);

    this.script = LuaScript.compile(buf, config.configContentName());
    this.cache = new WeakHashMap<>(MAX_CACHE_C);
    this.contentName = config.configContentName();
    this.structName = config.configStructName();
  }


  /**
   * 大小写敏感的搜索, 基于根目录
   */
  public FinderResult find(String content) {
    return find(DEF_BASE, content, true);
  }


  public FinderResult find(String basePath, String content) {
    return find(basePath, content, true);
  }


  /**
   * 在目录中搜索文本内容, 搜索结果将被缓存.
   *
   * @param basePath 搜索根目录
   * @param content 文本
   * @param caseSensitive 大小写敏感, true 效率更高
   * @return 返回含有指定内容的文件名列表
   */
  public FinderResult find(String basePath, String content, boolean caseSensitive) {
    String ckey = basePath + '/' + content + '/' + caseSensitive;
    FinderResult ret = cache.get(ckey);

    if (ret == null) {
      List<String> files = new ArrayList<>();
      String cursor = "0";
      boolean breakOnMaxCount = false;

      ScanParams sp = new ScanParams();
      sp.count(20);
      sp.match(basePath + "*");

      try (IRedis client = RedisMesmerizer.me().open()) {
        while (!breakOnMaxCount) {
          ScanResult<Map.Entry<String, String>>
                  res = client.hscan(structName, cursor, sp);
          cursor = res.getStringCursor();

          if (RedisMesmerizer.BEGIN_OVER_CURSOR.equals(cursor)) {
            break;
          }

          for (Map.Entry<String, String> fn : res.getResult()) {
            String filename = fn.getKey();
            Object findRes = script.eval(1,
                    contentName, caseSensitive, filename, content);

            if (findRes != null) {
              files.add(filename);
              if (files.size() > MAX_RESULT_COUNT) {
                breakOnMaxCount = true;
                break;
              }
            }
          }
        }
      }

      ret = new FinderResult(files, basePath, content, caseSensitive, breakOnMaxCount);
      cache.put(ckey, ret);
    }

    return ret;
  }


  /**
   * 清除所有缓存
   */
  public void clearCache() {
    cache.clear();
  }


  @Override
  public String toString() {
    return script.toString();
  }


}
