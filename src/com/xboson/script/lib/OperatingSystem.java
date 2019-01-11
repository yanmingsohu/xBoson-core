////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-1-11 下午12:37
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/lib/OperatingSystem.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script.lib;

import com.xboson.script.IVisitByScript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class OperatingSystem implements IVisitByScript {

  public final String EOL = "\n";
  public final Object constants;


  public OperatingSystem() {
    Map<String, Object> constants = new HashMap<>();
    this.constants = Collections.unmodifiableMap(constants);
  }


  public String arch() {
    return System.getProperty("os.arch");
  }


  public Object cpus() {
    return null;
  }


  public String endianness() {
    return null;
  }


  public long freemem() {
    return Runtime.getRuntime().freeMemory();
  }


  public int getPriority(int pid) {
    return 0;
  }


  public String homedir() {
    return null;
  }


  public Object loadavg() {
    return new int[] {0, 0, 0};
  }


  public Object networkInterfaces() {
    return null;
  }


  public String platform() {
    return System.getProperty("os.name");
  }


  public String release() {
    return System.getProperty("os.version");
  }


  public void setPriority(int pid, int priority) {
  }


  public String tmpdir() {
    return null;
  }


  public String tmpDir() {
    return null;
  }


  public long totalmem() {
    return Runtime.getRuntime().totalMemory();
  }


  public String type() {
    return System.getProperty("os.name");
  }


  public long uptime() {
    return System.nanoTime();
  }


  public Object userInfo() {
    return null;
  }
}
