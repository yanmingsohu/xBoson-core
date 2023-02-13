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
// 文件创建日期: 17-11-14 上午8:47
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/NameCache.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 存储 KEY_TYPE 对应的名字, 该对象适合在 static 中使用, 线程安全
 * @param <KEY_TYPE>
 */
public class NameCache<KEY_TYPE> {

  private Map<KEY_TYPE, String> map;


  public NameCache() {
    map = new ConcurrentHashMap<>();
  }


  public void put(KEY_TYPE k, String name) {
    map.put(k, name);
  }


  public String get(KEY_TYPE k) {
    if (k == null) return null;
    return map.get(k);
  }


  /**
   * 将 class 类型名字转换为简单可识别的类型字符串
   */
  public static String formatClassName(Class cl) {
    String fullname = cl.getName();
    String name;

    if (fullname.indexOf("java.") >= 0) {
      name = "primitive::" + toNoneJavaName(cl.getSimpleName());
    } else if (fullname.indexOf("xboson.") >= 0) {
      name = "xboson::" + toNoneJavaName(cl.getSimpleName());
    } else {
      name = "dynamic::" + toNoneJavaName(cl.getSimpleName());
    }
    return name;
  }


  /**
   * 将 java 命名规则的字符串, 转换为下划线命名规则
   */
  public static String toNoneJavaName(String name) {
    StringBuffer out = new StringBuffer(name.length() * 2);
    for (int i=0; i<name.length(); ++i) {
      char c = name.charAt(i);
      if (Character.isUpperCase(c)) {
        if (i != 0) {
          out.append('_');
        }
        out.append(Character.toLowerCase(c));
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }
}
