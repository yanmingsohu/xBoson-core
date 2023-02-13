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
// 文件创建日期: 17-12-19 上午9:30
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/JavaConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Java 常用数据类型转换
 */
public class JavaConverter {

  private JavaConverter() {}


  /**
   * 转换对象为字符串数组, 如果 objs 为空则返回 nullData.
   */
  public static String[] toStringArr(Object[] objs, String[] nullData) {
    if (objs == null)
      return nullData;

    String[] ps = new String[objs.length];
    for (int i = 0; i < ps.length; ++i) {
      ps[i] = (objs[i] == null) ? null : objs[i].toString();
    }
    return ps;
  }


  /**
   * 尝试将对象转换为 bool 值, 未知的类型将返回 false
   */
  public static boolean toBool(Object o) {
    if (o == null)
      return false;
    if (o instanceof String) {
      return Boolean.parseBoolean((String) o);
    }
    if (o instanceof Long) {
      return ((long) o) != 0;
    }
    if (o instanceof Double) {
      return ((double) o) != 0;
    }
    if (o instanceof Boolean) {
      return (boolean) o;
    }
    return false;
  }


  /**
   * 将 T 类型数组转换为 Set, 没有附加的处理
   */
  public static<T> Set<T> arr2set(T[] arr) {
    Set<T> set = new HashSet<>();
    for (int i=0; i<arr.length; ++i) {
      set.add(arr[i]);
    }
    return set;
  }


  /**
   * 比 arr2set 更适合进行构造
   */
  public static<T> Set<T> param2set(T... arr) {
    Set<T> set = new HashSet<>();
    for (int i=0; i<arr.length; ++i) {
      set.add(arr[i]);
    }
    return set;
  }


  /**
   * 转换为大写的 Set 集合
   */
  public static Set<String> arr2setUpper(String[] arr) {
    Set<String> set = new HashSet<>();
    for (int i=0; i<arr.length; ++i) {
      set.add(arr[i].toUpperCase());
    }
    return set;
  }


  /**
   * 转换为小写的 Set 集合
   */
  public static Set<String> arr2setLower(String[] arr) {
    Set<String> set = new HashSet<>();
    for (int i=0; i<arr.length; ++i) {
      set.add(arr[i].toLowerCase());
    }
    return set;
  }
}
