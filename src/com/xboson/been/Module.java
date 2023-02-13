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
// 文件创建日期: 2017年11月5日 下午1:53:54
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Module.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.script.IVisitByScript;
import com.xboson.util.Tool;


/**
 * js 运行后生成的模块.
 */
public class Module implements IBean, IVisitByScript {
  public String   id;
  public String   filename;
  public boolean  loaded;
  public Object   parent;
  public String[] paths;
  public int      loaderid;

  transient public Object children;
  transient public Object exports;


  public Module() {
    loaded = false;
  }


  public String toString() {
    return Tool.getAdapter(Module.class).toJson(this);
  }
}
