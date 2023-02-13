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
// 文件创建日期: 17-11-22 下午5:00
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/XjApi.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

import com.xboson.app.reader.AbsReadScript;
import com.xboson.app.reader.ScriptFile;
import com.xboson.auth.IAResource;
import com.xboson.auth.PermissionSystem;
import com.xboson.auth.impl.InvocationApi;
import com.xboson.been.CallData;
import com.xboson.been.Module;
import com.xboson.db.IDict;
import com.xboson.event.OnFileChangeHandle;
import com.xboson.fs.script.ScriptAttr;
import com.xboson.log.Log;
import com.xboson.log.LogFactory;

import javax.script.ScriptException;
import java.io.IOException;


/**
 * api 监听脚本修改事件并在脚本修改后清空缓存重新编译改变的脚本
 */
public class XjApi extends OnFileChangeHandle implements IDict, IAResource {

  private Log log;
  private XjOrg org;
  private XjApp app;
  private XjModule mod;
  private Module jsmodule;
  private ScriptFile file;
  private String id;
  private String res;
  private boolean isRequired;


  XjApi(XjOrg org, XjApp app, XjModule mod, String id) {
    this.org = org;
    this.app = app;
    this.mod = mod;
    this.id  = id;
    this.log = LogFactory.create("sc-core-api");
    this.res = (app.getID() + mod.id() + id).toLowerCase();

    readApiContent();
    regApiChangeListener();
    log.debug("Api Success", id);
  }


  private void readApiContent() {
    AbsReadScript reader = org.getScriptReader();
    file = reader.read(org, app.getID(), mod.id(), id);
  }


  private void regApiChangeListener() {
    String eventPath = ApiPath.getEventPath(
            AppContext.me().getApiModeType(),
            ApiPath.getPath(org.id(), app.getID(), mod.id(), id));
    regFileChange(eventPath);
  }


  /**
   * 该方法会执行编译操作, 必要时会读取源代码
   */
  public void run(CallData cd, String path) throws IOException, ScriptException {
    if (jsmodule == null) {
      synchronized (this) {
        if (jsmodule == null) {
          jsmodule = app.buildJSModule(path);
        }
      }
    }
    PermissionSystem.applyWithApp(InvocationApi.class, this);
    app.run(cd, jsmodule, this);
  }


  @Override
  protected void onFileChange(String file_name_not_use) {
    synchronized (this) {
      jsmodule = null;
      file = null;
      app.updateApiScript(this);
      log.debug("Script Changed:", mod.id(), '/', id);

      //
      // 被引用脚本改变后需要立即重新编译.
      //
      if (isRequired) {
        try {
          jsmodule = app.buildJSModule(ApiPath.toFile(mod.id(), id));
        } catch (IOException | ScriptException e) {
          log.error("Reloading script fail,", e.toString());
        }
      }
    }
  }


  private ScriptFile getFile() {
    if (file == null) {
      synchronized(this) {
        if (file == null){
          readApiContent();
        }
      }
    }
    return file;
  }


  public byte[] getCode() {
    return getFile().content;
  }


  public ScriptAttr getApiAttr() {
    return getFile().attr;
  }


  @Override
  public String description() {
    return res;
  }


  /**
   * 设置脚本的是使用 require 加载的
   */
  void setRequired(boolean is) {
    isRequired = is;
  }


  boolean isRequired() {
    return isRequired;
  }


  public void fillOriginalApiCode(AppContext.ThreadLocalData tld) {
    ScriptFile sf = getFile();
    tld.setOriginalApiCode(sf.original_code, sf.original_hash);
  }
}
