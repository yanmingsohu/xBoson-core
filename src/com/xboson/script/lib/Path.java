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
