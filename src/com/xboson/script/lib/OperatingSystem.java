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
