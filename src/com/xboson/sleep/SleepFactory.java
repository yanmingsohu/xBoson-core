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
// 文件创建日期: 17-11-11 上午11:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/sleep/SleepFactory.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.sleep;

import java.util.HashMap;
import java.util.Map;


/**
 * 当有其他用途时再进行设计
 *
 * @deprecated 通常直接使用 RedisMesmerizer
 * @see RedisMesmerizer
 */
public class SleepFactory {

  private static SleepFactory instance;

  static {
    instance = new SleepFactory();
  }


  /**
   * 需要考虑线程安全
   * @return
   */
  public static SleepFactory me() {
    return instance;
  }


///////////////////////////////////////////////////////////////////////////////

  private Map<Class<?>, IMesmerizer> map;
  private IMesmerizer default_mes;


  private SleepFactory() {
    map = new HashMap<>();
  }


  /**
   * 配置对象类型的持久化算法
   */
  public void config(Class<?> type, IMesmerizer mes) {
    if (type == null)
      throw new NullPointerException("Class");
    if (mes == null)
      throw new NullPointerException("IMesmerizer");
    map.put(type, mes);
  }


  public void configDefault(IMesmerizer mes) {
    if (mes == null)
      throw new NullPointerException("IMesmerizer");
    default_mes = mes;
  }


  public IMesmerizer getMesmerizer(Class<?> c) {
    IMesmerizer mes = map.get(c);
    if (mes == null) {
      throw new NullPointerException("cannot config any Mesmerizer");
    }
    return mes;
  }


  public IMesmerizer getMesmerizer() {
    if (default_mes == null) {
      throw new NullPointerException("cannot get default Mesmerizer");
    }
    return default_mes;
  }

}
