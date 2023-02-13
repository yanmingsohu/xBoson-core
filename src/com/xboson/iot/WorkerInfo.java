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
// 文件创建日期: 20-11-23 上午7:05
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/iot/WorkerInfo.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.iot;

import com.xboson.script.IVisitByScript;


public class WorkerInfo implements IVisitByScript {
  public String name;
  // mqtt 主题字符串
  public String topic;
  // 工作节点
  public short node;
  // 线程id, 总是从 0 开始, 来自索引
  public long tid;
  // 处理数据量
  public long count;
  // 最后数据时间
  public long time;
  // 状态描述
  public String stateMsg;
  // 出错数量
  public long error;
  // 数据质量
  public String qos;
  // cpu 占用率
  public int cpu;
}
