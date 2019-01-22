////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 中文字符串变英文首字母串, 带缓存
 */
public class ChineseDictionary {

  /**
   * 只保存中文对应的拼音首字母
   */
  private static Map<Character, String> firstCache;

  private final static String PY_DICT_FILE = "pinyin.prop";
  private final static String NULL_STR = IConstant.NULL_STR;
  private final static char SP = ' ';


  /**
   * 从文件中加载中文拼音字典
   */
  public synchronized static void init() {
    if (firstCache == null) {
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

        Map<Character, String> _map = new HashMap<>(prop.size());
        for (Map.Entry<Object, Object> ent : prop.entrySet()) {
          String cn = ent.getKey().toString();
          String en = ent.getValue().toString();
          if (en.length() > 0) {
            _map.put(cn.charAt(0), en);
          } else {
            _map.put(cn.charAt(0), NULL_STR);
          }
        }
        firstCache = _map;
        log.info("init success");
      } catch (Exception e) {
        log.error(e);
      }
    }
  }


  /**
   * 取得给定汉字串的首字母串
   */
  public static String toFirstPinYinLetter(String str) {
    if (Tool.isNulStr(str)) {
      return NULL_STR;
    }
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < str.length(); ++i) {
      Character cn = str.charAt(i);
      String py = firstCache.get(cn);
      if (py != null && py != NULL_STR) {
        out.append(py.charAt(0));
      } else if (cn < 127) {
        out.append((char) cn);
      }
    }
    return out.toString();
  }


  /**
   * 返回给定汉字完整拼音字串, 每个字用空格分隔
   */
  public static String toFullPinYinLetter(String str) {
    if (Tool.isNulStr(str)) {
      return NULL_STR;
    }
    StringBuilder out = new StringBuilder();
    int state = 0;

    for (int i = 0; i < str.length(); ++i) {
      Character cn = str.charAt(i);
      String py = firstCache.get(cn);

      if (py != null && py != NULL_STR) {
        if (state != 0) {
          out.append(SP);
        }
        state = 2;
        out.append(py);
      }
      else if (cn < 127) {
        if (state != 1) {
          if (state > 0) out.append(SP);
          state = 1;
        }
        out.append((char) cn);
      }
    }
    return out.toString();
  }
}