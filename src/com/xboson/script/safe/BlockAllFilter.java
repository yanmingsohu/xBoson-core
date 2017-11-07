package com.xboson.script.safe;

import jdk.nashorn.api.scripting.ClassFilter;


public class BlockAllFilter implements ClassFilter {
  @Override
  public boolean exposeToScripts(String paramString) {
    // System.out.println(paramString + " @@!");
    return false;
  }
}
