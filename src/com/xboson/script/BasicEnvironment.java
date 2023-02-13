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
/* CatfoOD  yanming-sohu@sohu.com Q.412475540 */
////////////////////////////////////////////////////////////////////////////////
//
// 文件创建日期: 2017年11月5日 上午11:33:42
// 原始文件路径: xBoson/src/com/xboson/script/BasicEnvironment.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

import java.util.*;

import javax.script.Bindings;
import javax.script.ScriptException;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.Tool;


/**
 * 创建基础 js 环境, 并提供可配置的接口,
 * 含有网络连接的复杂对象环境可以继承该类.
 */
public class BasicEnvironment implements IEnvironment {

  private final Log log;
  private List<IJSObject> objs;
  private Set<IConfigSandbox> configer;
  private IModuleProvider sys_mod;


  /**
   * 创建一个空环境
   */
  public BasicEnvironment() {
    log      = LogFactory.create();
    objs     = new ArrayList<>(30);
    configer = new HashSet<>(10);
  }


  /**
   * 创建环境并将一个沙箱配置器加入环境中
   * @param p
   */
  public BasicEnvironment(IConfigSandbox p) {
    this();
    insertConfiger(p);
  }


  public void insertConfiger(IConfigSandbox cs) {
    configer.add(cs);
  }


  public void setEnvObjectList(Class<? extends IJSObject>[] list) {
    for (int i=0; i<list.length; ++i) {
      setEnvObject(list[i]);
    }
  }


  public void setEnvObject(Class<? extends IJSObject> c) {
    try {
      IJSObject jso = (IJSObject) c.newInstance();
      jso.init();
      objs.add(jso);
    } catch(Exception e) {
      log.error(e.getMessage());
    }
  }


  public void config(Sandbox box, ICodeRunner runner) throws ScriptException {
    Bindings bind = box.getBindings();

    //
    // 配置 js 库对象.
    //
    Iterator<IJSObject> it = objs.iterator();
    while (it.hasNext()) {
      IJSObject o = it.next();
      String name = o.env_name();
      bind.put(name, o);
    }

    //
    // 使用配置器配置沙箱
    //
    Iterator<IConfigSandbox> it_config = configer.iterator();
    while (it_config.hasNext()) {
      IConfigSandbox ics = it_config.next();
      try {
        ics.config(box, runner);
      } catch (Exception e) {
        log.error("Config sanbox", ics, Tool.allStack(e));
      }
    }
  }


  public void destory() {
    Iterator<IJSObject> it = objs.iterator();

    while (it.hasNext()) {
      IJSObject o = it.next();
      try {
        o.destory();
      } catch(Exception e) {
        log.error("Destory", o, e);
      }
      it.remove();
    }

    configer = null;
    objs = null;
  }


  @Override
  protected void finalize() throws Throwable {
    destory();
  }

}
