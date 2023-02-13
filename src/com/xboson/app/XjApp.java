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
// 文件创建日期: 17-11-13 上午11:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/OrgApp.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.been.CallData;
import com.xboson.been.Module;
import com.xboson.been.UrlSplit;
import com.xboson.been.XBosonException;
import com.xboson.db.IDict;
import com.xboson.db.SqlResult;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.fs.script.IScriptFileSystem;
import com.xboson.script.Application;
import com.xboson.script.NotFoundModuleMaySkip;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * 每个 app 共享唯一的沙箱, 并缓存编译好的模块
 */
public class XjApp extends XjPool<XjModule> implements IDict, IScriptFileSystem {

  public final static long MODULE_SKIP_TIME = 10* 1000;

  private ServiceScriptWrapper ssw;
  private Application runtime;
  private XjOrg org;
  private String name;
  private String id;
  private Map<String, Object> cacheData;
  private Map<String, Long> skipModule;


  XjApp(XjOrg org, String id) {
    this.org = org;
    this.id = id;
    this.cacheData = Collections.synchronizedMap(new WeakHashMap<>());
    this.skipModule = new WeakHashMap<>();
    init_app();

    try {
      this.ssw = new ServiceScriptWrapper();
      runtime = new Application(ssw.getEnvironment(), this);
    } catch (IOException|ScriptException e) {
      throw new XBosonException(e);
    }

    log.debug("App success", id);
  }


  Module buildJSModule(String path)
          throws IOException, ScriptException {
    return runtime.run(path);
  }


  void run(CallData cd, Module jsmodule, XjApi api) {
    ssw.run(cd, jsmodule, org, this, api);
  }


  public void run(CallData cd, String module_id, String api_id,
                  AppContext.ThreadLocalData tld)
          throws IOException, ScriptException {
    XjModule mod = getWithCreate(module_id);
    XjApi api = mod.getApi(api_id);
    api.fillOriginalApiCode(tld);
    api.run(cd, ApiPath.toFile(module_id, api_id));
  }


  private void init_app() {
    try (SqlResult res = org.query("open_app.sql", id)) {
      ResultSet rs = res.getResult();
      if (rs.next()) {
        if (!ZR001_ENABLE.equals(rs.getString("status"))) {
          throw new XBosonException("应用已经禁用");
        }
        name = rs.getString("appnm");
      } else {
        throw new XBosonException("找不到应用 " + id, 1202);
      }
    } catch (SQLException e) {
      throw new XBosonException(e);
    }
  }


  @Override
  protected XjModule createItem(String id) {
    Long time = skipModule.get(id);
    if (time != null) {
      if (System.currentTimeMillis()-time < MODULE_SKIP_TIME) {
        //
        // 在 MODULE_SKIP_TIME 时间内不再检测 module 是否存在而是直接认为不存在,
        // 脚本加载器在 require() 时会频繁访问同一个模块, 创建模块时需要访问 DB,
        // 使用缓存记录不存在的模块可以大大加快脚本加载器的速度.
        //
        throw new NotFoundModuleMaySkip(id);
      } else {
        skipModule.remove(id);
      }
    }

    try {
      return new XjModule(org, this, id);
    } catch (NotFoundModuleMaySkip e) {
      skipModule.put(id, System.currentTimeMillis());
      throw e;
    }
  }


  private XjApi getApi(String path) {
    UrlSplit sp = new UrlSplit(path);
    sp.withoutSlash(true);

    XjModule mod = super.getWithCreate(sp.getName());
    return mod.getWithCreate(sp.getLast());
  }


  /**
   * 同步: 防止同一个脚本加载两次.
   */
  @Override
  public synchronized ByteBuffer readFile(String path) throws IOException {
    XjApi api = getApi(path);
    //
    // 在同一个上下文加载两次脚本, 则认为除了第一次之外的脚本都是通过 require 加载的.
    //
    if (api.isRequired() || AppContext.me().isRequired()) {
      log.debug("require() ->", path);
      api.setRequired(true);
      return ByteBuffer.wrap(api.getCode());
    }
    return ssw.wrap(api.getCode());
  }


  @Override
  public ScriptAttr readAttribute(String path) throws IOException {
    XjApi api = getApi(path);
    return api.getApiAttr();
  }


  /**
   * 通知 app 脚本内容修改
   */
  public void updateApiScript(XjApi api) {
    runtime.changed(api.getApiAttr().fullPath);
  }


  @Override
  public String getID() {
    return id;
  }


  @Override
  public String getType() {
    return "Script-FS";
  }


  /**
   * 应用范围内的缓存.
   * 返回的集合用于在脚本中保存一些常用数据, 支持多线程.
   * 不能保证一定能取出之前压入的值.
   */
  public Map<String, Object> getCacheData() {
    return cacheData;
  }


  @Override
  public String logName() {
    return "sc-core-app";
  }

}
