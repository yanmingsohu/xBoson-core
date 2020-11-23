////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
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
