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
// 文件创建日期: 17-12-11 下午2:10
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/ModuleHandleContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.script.IJSObject;
import com.xboson.util.CloseableSet;
import com.xboson.util.Tool;

import java.util.HashMap;
import java.util.Map;


/**
 * 在外部模块和服务脚本上下文之间传递对象, 线程级别的.
 * [不要传递不安全的对象]
 */
public class ModuleHandleContext implements IJSObject {

  /**
   * 注册一个该名称的 CloseableSet 对象到上下文,
   * 之后可以调用 autoClose 来自动关闭可关闭对象.
   * @see CloseableSet
   */
  public static final String CLOSE = "close_set";

  private static ThreadLocal<Map<String, Object>> moduleHandle;


  private static Map<String, Object> getMap() {
    Map<String, Object> map = moduleHandle.get();
    if (map == null) {
      map = new HashMap<>();
      moduleHandle.set(map);
    }
    return map;
  }


  /**
   * 在脚本中调用, 获取当前脚本上下文中 name 对象的引用
   */
  public Object get(String name) {
    return _get(name);
  }


  /**
   * 注册 SqlImpl 对象到当前上下文, 之后便可以引用
   */
  public static void register(String name, Object val) {
    getMap().put(name, val);
  }


  /**
   * 当对象退出脚本上下文后被关闭
   */
  public static void autoClose(AutoCloseable ac) {
    CloseableSet c = (CloseableSet) _get(CLOSE);
    if (c == null)
      throw new XBosonException.NotFound("Not found CloseableSet");
    c.add(ac);
  }


  public static Object _get(String name) {
    Object modimpl = getMap().get(name);
    if (modimpl == null) {
      throw new XBosonException.NotExist("Cannot get '" + name
              +"' Module, Maybe current user not enough authority,"
              +" or not in Application context.");
    }
    return modimpl;
  }


  @Override
  public String env_name() {
    return "moduleHandleContext";
  }


  @Override
  public boolean freeze() {
    return true;
  }


  @Override
  public void init() {
    if (moduleHandle == null) {
      moduleHandle = new ThreadLocal<>();
    }
  }


  @Override
  public void destory() {
    moduleHandle = null;
  }


  /**
   * 返回的关闭类在线程退出时调用.
   */
  public static void exitThread() {
    if (moduleHandle == null) return;
    Map<String, Object> map = moduleHandle.get();
    if (map != null) {
      map.clear();
    }
  }
}
