////////////////////////////////////////////////////////////////////////////////
//
// Copyright 2019 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
// 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
// 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
// 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
// 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
//
// 文件创建日期: 19-1-20 上午9:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/init/SystemFlag.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.init;

/**
 * 该对象作为全局变量使用, 其中的属性可以在外部直接修改;
 * 设计原则: 不可以存储复杂对象, 属性值本身必须是不可变的, 不可以依赖外部包/类
 * 引用该对象不会因为不存在的 package/class 而崩溃.
 */
public final class SystemFlag {
  private SystemFlag() {/* 不可实例化 */}


  /**
   * 如果需要 servlet 退出后立即重新启动, 则设置这个变量为 true;
   * 该属性只在独立运行模式有效; 一旦设置为 true, 当程序退出, 一个新的克隆进程将被启动.
   */
  public static boolean autoRestart = false;


  /**
   * 该标记为 true, 表明平台可以正确安全的重启.
   */
  public static boolean canRestart = false;

}
