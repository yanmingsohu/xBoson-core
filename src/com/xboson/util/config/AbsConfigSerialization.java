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
// 文件创建日期: 18-1-9 下午5:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/config/AbsConfigSerialization.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.config;

import java.util.Arrays;
import java.util.Map;


public abstract class AbsConfigSerialization implements IConfigSerialization {

  /**
   * 在 json 中加入注释, 实现效率很低, 返回的 json 是不符合规范的.
   */
  public final String addComments(String json) {
    StringBuilder buf = new StringBuilder();
    for (Map.Entry entry : DefaultConfig.comments().entrySet()) {
      String find = find((String) entry.getKey());
      int i = 0;

      while (i < json.length()) {
        i = json.indexOf(find, i);

        if (i == 0 || (i>0 && Character.isWhitespace(json.charAt(i-1)))) {
          int a = json.lastIndexOf("\n", i);

          if (a >= 0) {
            buf.setLength(0);
            char[] sp = new char[i-a-1];
            Arrays.fill(sp, ' ');

            buf.append(json.substring(0, a))
                    .append("\n\n")
                    .append(sp)
                    .append(beginComment())
                    .append(entry.getValue())
                    .append(endComment());

            i = sp.length + buf.length() + find.length();
            buf.append(json.substring(a));
            json = buf.toString();
          } else {
            i += find.length();
          }
        } else {
          break;
        }
      }
    }
    return json;
  }


  /**
   * 删除注释, 返回标准 json 字符串
   */
  public abstract String reomveComments(String json);


  /**
   * 返回注释开始字符
   */
  public abstract String beginComment();


  /**
   * 返回注释结束字符
   */
  public abstract String endComment();


  /**
   * 查询 key 名称的配置, 对查询字符串进行格式化并返回.
   */
  public abstract String find(String key);
}
