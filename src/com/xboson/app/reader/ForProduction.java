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
// 文件创建日期: 17-12-16 上午10:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/reader/ForProduction.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.reader;


import com.squareup.moshi.JsonAdapter;
import com.xboson.app.ApiPath;
import com.xboson.app.XjOrg;
import com.xboson.been.XBosonException;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.sleep.IRedis;
import com.xboson.sleep.RedisMesmerizer;
import com.xboson.util.Tool;
import redis.clients.jedis.Jedis;

import java.util.Date;


public class ForProduction extends AbsReadScript {

  public final static String REGION = _R_KEY_PREFIX_ + _CACHE_REGION_API_;


  @Override
  public ScriptFile read(XjOrg org, String app, String mod, String api) {
    log.debug("Script From Redis", mod, api);

    try (IRedis j = RedisMesmerizer.me().open()) {
      String key = (app + mod + api).toLowerCase();
      String json = j.hget(REGION, key);

      if (json != null) {
        //
        // json = [ content, orgid, zip ]
        //
        Object[] apiSet = Tool.getAdapter(Object[].class).fromJson(json);
        String content  = (String) apiSet[0];
        int zip = (apiSet.length < 3) ? 0 : ((Double)apiSet[2]).intValue();

        Date now        = new Date();
        ScriptAttr attr = new ScriptAttr();
        ScriptFile file = makeFile(attr, content, zip);
        attr.fileSize   = file.content.length;
        attr.fileName   = api;
        attr.pathName   = '/' + mod;
        attr.fullPath   = ApiPath.toFile(mod, api);
        attr.createTime = now.getTime();
        attr.modifyTime = now.getTime();

        log.debug("Load Script from CACHE:", mod, '/', api);
        return file;
      }
    } catch (Exception e) {
      log.warn("Script from Redis fail", mod, api, e);
    }
    throw new XBosonException.NotFound("API:" + api);
  }


  @Override
  public String logName() {
    return "read-product-api";
  }
}
