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
// 文件创建日期: 17-11-24 下午5:39
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ChineseDictionary.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;


import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import com.xboson.util.c0nst.IConstant;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;


/**
 * 中文字符串变英文首字母串, 带缓存
 */
public class ChineseDictionary {

  private final static String PY_DICT_FILE = "pinyin.prop";
  private final static char SP = ' ';

  private static int max = 0;
  private static int min = Integer.MAX_VALUE;
  private static String[] dict;


  /**
   * 从文件中加载中文拼音字典
   */
  public synchronized static void init() {
    if (dict == null) {
      Log log = LogFactory.create("pinyin-init");
      try {
        //
        // 读取的是完整拼音
        //
        StringBufferOutputStream buf =
            Tool.readFileFromResource(ChineseDictionary.class, PY_DICT_FILE);
        StringReader utf8reader = new StringReader(buf.toString());
        Properties prop = new Properties();
        prop.load(utf8reader);

        for (Object n : prop.keySet()) {
          char c = ((String)n).charAt(0);
          if (min > c) min = c;
          if (max <= c) max = c+1;
        }

        String[] _map = new String[max - min];

        for (Map.Entry<Object, Object> ent : prop.entrySet()) {
          String cn = ent.getKey().toString();
          String en = ent.getValue().toString();

          char c = cn.charAt(0);
          if (en.length() > 0) {
            _map[c - min] = en;
          }
        }

        dict = _map;
        log.info("init success", min, max, max-min);
      } catch (Exception e) {
        log.error(e, "min:", min, "max:", max);
      }
    }
  }


  /**
   * 取得给定汉字串的首字母串
   */
  public static String toFirstPinYinLetter(String str) {
    if (Tool.isNulStr(str)) {
      return IConstant.NULL_STR;
    }
    StringBuilder out = new StringBuilder();

    for (int i = 0; i < str.length(); ++i) {
      char cn = str.charAt(i);

      if (cn >= min && cn < max) {
        String py = dict[cn - min];
        if (py != null) {
          out.append(py.charAt(0));
        }
      } else if (cn < 127) {
        out.append(cn);
      }
    }
    return out.toString();
  }


  /**
   * 返回给定汉字完整拼音字串, 每个字用空格分隔
   */
  public static String toFullPinYinLetter(String str) {
    if (Tool.isNulStr(str)) {
      return IConstant.NULL_STR;
    }
    StringBuilder out = new StringBuilder();
    int state = 0;

    for (int i = 0; i < str.length(); ++i) {
      char cn = str.charAt(i);

      if (cn >= min && cn <= max) {
        String py = dict[cn - min];

        if (py != null) {
          if (state != 0) {
            out.append(SP);
          }
          state = 2;
          out.append(py);
        }
      }
      else if (cn < 127) {
        if (state != 1) {
          if (state > 0) out.append(SP);
          state = 1;
        }
        out.append(cn);
      }
    }
    return out.toString();
  }
}