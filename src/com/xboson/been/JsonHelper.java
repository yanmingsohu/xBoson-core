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
// 文件创建日期: 17-11-13 上午9:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/JsonHelper.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.util.Tool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * 帮助对象 json 化,
 * 该实现尽可能不增加子类的内存负担
 */
public abstract class JsonHelper implements IBean, IJson {

  public final static int F_MOD =
          Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE;

  /**
   * 别名: moshi 使用这样的命名
   * 该方法不需要重写, 只要重写 toJSON 即可.
   */
  public final String toJson() {
    return toJSON();
  }


  /**
   * 可以正确处理深层对象
   */
  @Override
  public String toJSON() {
    return toJSON(this);
  }


  public static String toJSON(Object o) {
    return Tool.getAdapter((Class) o.getClass()).toJson(o);
  }


  /**
   * 输出所有属性, 方便调试
   */
  public String toString() {
    return toString(this);
  }


  public static String toString(Object o) {
    StringBuilder out = new StringBuilder();
    Field[] fs = o.getClass().getDeclaredFields();
    out.append(o.getClass());

    for (int i=0; i<fs.length; ++i) {
      Field f = fs[i];
      f.setAccessible(true);
      out.append("\n\t");
      out.append(f.getName());
      out.append(" - ");
      try {
        Object v = f.get(o);
        if (v == null) {
          out.append("NULL");
        } else if (v.getClass().isPrimitive()) {
          out.append(v);
        } else {
          out.append(toJSON(v));
        }
      } catch (IllegalAccessException e) {
        out.append(e.getMessage());
      }
    }
    return out.toString();
  }
}
