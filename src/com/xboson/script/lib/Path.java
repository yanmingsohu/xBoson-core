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


import com.xboson.been.XBosonException;
import com.xboson.util.c0nst.IConstant;
import com.xboson.util.Tool;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

public class Path {

  public static final String R = "/";
  public static final Path me = new Path();


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
    if (p.indexOf("../") >= 0) {
      throw new IllegalArgumentException("path block '/../' string");
    }
  }


  public String join(String... s) {
    StringBuilder buf = new StringBuilder();
    for (int i=0;;) {
      buf.append(s[i]);
      if (++i < s.length) {
        buf.append('/');
      } else {
        break;
      }
    }
    return normalize(buf.toString());
  }


  public String resolve(String... s) {
    String r = join(s);
    if (Tool.isNulStr(r)) return R;
    if (r.charAt(0) != '/') r = '/'+ r;
    return r;
  }


  public String relative(String ap, String bp) {
    LinkedList<String> pa = split(ap);
    LinkedList<String> pb = split(bp);

    StringBuilder out = new StringBuilder();
    int len = pa.size();
    int sameIndex;

    for (sameIndex=0; sameIndex < len; ++sameIndex) {
      if (! pa.get(sameIndex).equals( pb.get(sameIndex) )) {
        break;
      }
    }

    for (int i=len-sameIndex; i>0; --i) {
      out.append("../");
    }

    len = pb.size();
    if (sameIndex < len) {
      for (;;) {
        out.append(pb.get(sameIndex));
        if (++sameIndex < len) out.append('/');
        else break;
      }
    }
    return out.toString();
  }


  public String normalize(String s) {
    LinkedList<String> buf = split(s);
    int a = 0;
    int b = buf.size();

    if (b == 1 && IConstant.NULL_STR.equals(buf.get(0))) {
      return R;
    } else if (b > 0) {
      StringBuilder out = new StringBuilder();
      for (;;) {
        out.append(buf.get(a));
        if (++a < b) {
          out.append('/');
        } else {
          break;
        }
      }
      return out.toString();
    }
    return IConstant.NULL_STR;
  }


  private LinkedList<String> split(String s) {
    LinkedList<String> buf = new LinkedList<>();
    char ch;
    int a = 0, b = -1, i, word = 0;

    for (i=0; i<s.length(); ++i) {
      ch = s.charAt(i);
      if (ch == '/' || ch == '\\') {
        if (b == -1 && (a==0 || a!=i)) {
          buf.addLast(s.substring(a, i));
          ++word;
        } else if (b == 1) {
          if (word > 0) {
            buf.pollLast();
            --word;
          } else {
            buf.addLast("..");
          }
        }
        b = -1;
        a = i+1;
      }
      else if (ch == '.') {
        ++b;
      }
      else {
        b = -1;
      }
    }

    if (a < s.length()) {
      if (b == 1 && word > 0) {
        buf.pollLast();
      } else {
        buf.addLast(s.substring(a, s.length()));
      }
    }
    return buf;
  }


  /**
   * 返回除名称之外的完整父目录,
   * 如果没有父路径返回 null.
   */
  public String dirname(String path) {
    char[] ch = path.trim().toCharArray();
    int end = ch.length - 1;

    //
    // 去掉末尾连续的 '/'
    //
    while (end >= 0 && ch[end] == '/') {
      --end;
    }

    //
    // 找到末尾开始第一个 '/'
    //
    while (end >= 0 && ch[end] != '/') {
      --end;
    }

    if (end < 0)
      return null;

    if (end == 0)
      return "/";

    return new String(ch, 0, end);
  }


  public String extname(String path) {
    int i = path.length() - 1;
    while (i >= 0) {
      char ch = path.charAt(i);
      if (ch == '/' || ch == '\\') {
        return IConstant.NULL_STR;
      }
      if (ch == '.') break;
      --i;
    }
    if (i > 0) {
      return path.substring(i);
    }
    return IConstant.NULL_STR;
  }


  /**
   * 只返回文件部分, 目录被丢弃, 目录格式无效返回 null
   */
  public String basename(String path) {
    char[] ch = path.trim().toCharArray();
    int end = ch.length - 1;

    //
    // 去掉末尾连续的 '/'
    //
    while (end >= 0 && ch[end] == '/') {
      --end;
    }

    //
    // 找到末尾开始第一个 '/'
    //
    while (end >= 0 && ch[end] != '/') {
      --end;
    }


    if (++end < ch.length)
      return new String(ch, end, ch.length - end);

    return null;
  }


  public String basename(String path, String ext) {
    String bn = basename(path);
    if (bn.endsWith(ext)) {
      return bn.substring(0, bn.length() - ext.length());
    }
    return bn;
  }


  /**
   * path 是绝对路径返回 true
   */
  public boolean isAbsolute(String path) {
    if (Tool.isNulStr(path))
      return false;

    char c = path.charAt(0);
    return c == '/' || c == '\\';
  }


  /**
   * 将路径格式化为类路径, 无效的路径返回 null.
   */
  public String toClassPath(String path) {
    if (!path.endsWith(".class"))
      throw new XBosonException.BadParameter(
              "String path", "not class file: " + path);

    final int size = path.length() - 6;
    if (size <= 0) return null;
    StringBuilder buf = new StringBuilder(size);
    int i;
    char ch;
    int state = 0;

    //
    // 跳过前置 '/' 或 '\'
    //
    for (i = 0; i < size; ++i) {
      ch = path.charAt(i);
      if (ch != '/' && ch != '\\') break;
    }

    for (; i < size; ++i) {
      ch = path.charAt(i);
      if (ch == '/' || ch == '\\') {
        // 连续的 '/' 被合并为一个 '.'
        if (state == 1) continue;
        buf.append('.');
        state = 1;
      } else {
        buf.append(ch);
        state = 0;
      }
    }
    return buf.toString();
  }
}
