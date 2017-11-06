package com.xboson.script.env;


public class Path {


  public Path() {
  }


  public String normalize(String s) {
    String [] ss = s.split("/");

    for (int i=0; i<ss.length; ++i) {
      if (ss[i].equals(".")) {
        ss[i] = null;
      } else if (ss[i].equals("..")) {
        ss[i] = null;
        if (i-1 >= 0) {
          ss[i - 1] = null;
        }
      }
    }

    StringBuilder out = new StringBuilder();
    for (int i=0; i<ss.length; ++i) {
      String b = ss[i];
      if (b != null) {
        if (i!=0) out.append("/");
        out.append(b);
      }
    }

    return out.toString();
  }

}
