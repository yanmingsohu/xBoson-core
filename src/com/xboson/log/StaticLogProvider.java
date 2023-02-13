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
// 文件创建日期: 17-12-19 下午2:04
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/log/StaticLogProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.log;

import com.xboson.been.XBosonException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * 为纯静态类提供日志创建服务, 普通类不要用.
 * 该类创建的日志对象在不常使用后会被自动回收.
 * 日志对象仅在调用了 openLog() 后被创建.
 *
 * @see LogFactory#create() 普通类构建日志的方法
 */
public class StaticLogProvider {

  private static Map<String, Log> classMap =
          Collections.synchronizedMap(new WeakHashMap<>());


  /**
   * 打开日志, 不推荐
   *
   * @see #openLog(Class) 推荐
   * @deprecated 会从错误堆栈中取出对象名作为日志名称, 效率低
   */
  protected static Log openLog() {
    Exception e = new Exception();
    StackTraceElement[] t = e.getStackTrace();
    return openLog(t[1].getClassName());
  }


  /**
   * 打开日志, 该方法效率较高
   */
  protected static Log openLog(Class c) {
    return openLog(c.getName());
  }


  /**
   * 打开日志, 该方法效率较高
   */
  protected static Log openLog(String className) {
    if (className == null)
      throw new XBosonException.NullParamException("String className");

    Log log = classMap.get(className);
    if (log == null) {
      synchronized (StaticLogProvider.class) {
        log = classMap.get(className);
        if (log == null) {
          log = LogFactory.create(className);
          classMap.put(className, log);
        }
      }
    }
    return log;
  }
}
