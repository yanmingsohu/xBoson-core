////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2017 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 17-12-13 下午6:31
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/ISState.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

import java.io.OutputStream;
import java.util.Map;


/**
 * 前向状态机接口
 */
public interface ISState {
  /** 返回初始状态, 并把之前的字符序列写入输出. */
  int RESET = 4;
  /** 进入状态机, 一旦发生 RESET 将重制到 BEGIN 发生的点. */
  int BEGIN = 1;
  /** 保持当前状态机 */
  int KEEP  = 2;
  /** 没有特别的动作 */
  int NOTHING = 0;
  int INIT = 0;
  /** 进入下一个状态 */
  int NEXT  = 1000;
  /** 进入下一个状态, 并退回一个字符 */
  int NEXT_AND_BACK = 1001;
  /** 进入下一个状态, 并将字符退回到状态机的起始位置 */
  int NEXT_AND_BACK_ALL = 1002;

  /**
   * 读取一个字符, 返回下一次状态
   */
  int read(byte ch);
  void setData(String[] strarr);
  void setOutput(OutputStream out);
}
