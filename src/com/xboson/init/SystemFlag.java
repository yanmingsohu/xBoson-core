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
