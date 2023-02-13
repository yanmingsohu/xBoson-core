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
// 文件创建日期: 18-1-28 上午10:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/PublicProcessData.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.script.IVisitByScript;
import com.xboson.util.Tool;


/**
 * 进程公共数据, 全部 public 属性.
 * 记录着调用 api 的必要数据.
 */
public class PublicProcessData implements IJson, IVisitByScript {

  public long processId;
  public String org;
  public String app;
  public String mod;
  public String api;
  public long beginAt;
  public long runningTime;
  public String callUser;
  public String nodeID;


  public PublicProcessData() {}


  @Override
  public String toJSON() {
    return Tool.beautifyJson(PublicProcessData.class, this);
  }
}
