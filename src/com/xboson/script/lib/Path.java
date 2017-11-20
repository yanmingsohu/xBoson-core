////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 2017年11月6日 11:09
// 原始文件路径: xBoson/src/com/xboson/script/lib/Path.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;


import java.util.Iterator;
import java.util.LinkedList;

public class Path {


  /**
   * 不安全的路径会抛出异常
   */
  public void checkSafe(String p) {
    if (p.indexOf('\\') >= 0) {
      throw new IllegalArgumentException("path block '\\' char");
    }
    if (p.indexOf("/../") >= 0) {
      throw new IllegalArgumentException("path block '/../' string");
    }
  }


  public String normalize(String s) {
    LinkedList<String> buf = new LinkedList<>();
    char ch;
    int a = 0, b = -1, i;

    for (i=0; i<s.length(); ++i) {
      ch = s.charAt(i);
      if (ch == '\\') {
        continue;
      }
      else if (ch == '/') {
        if (b == -1) {
          buf.addLast(s.substring(a, i));
          b = i;
        } else if (b-a == 2) {
          buf.pollLast();
        } else if (a == b) {
          b = i;
        } else {
          b = -1;
        }
        a = i;
      }
      else if (ch == '.') {
        ++b;
      }
      else {
        b = -1;
      }
    }

    if (a < s.length()) {
      buf.addLast(s.substring(a, s.length()));
    }

    StringBuilder out = new StringBuilder();
    Iterator<String> it = buf.iterator();
    while (it.hasNext()) {
      out.append( it.next() );
    }
    return out.toString();
  }

}
