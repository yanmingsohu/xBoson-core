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
// 文件创建日期: 18-3-16 上午10:01
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/script/EventFlag.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.script;

/**
 * 脚本事件标记, 全部为公共终结属性.
 * 实现需要自行维护标记的重置
 */
public class EventFlag implements IVisitByScript {

  public final static EventFlag me = new EventFlag();

  /**
   * 脚本被 require() 引入
   */
  public final int IN_REQUIRE = 1;

  /**
   * 引入结束
   */
  public final int OUT_REQUIRE = 4;

  /**
   * 当脚本首次执行一次后退出.
   */
  public final int SCRIPT_OUT = 2;

  /**
   * 当脚本首次执行
   */
  public final int SCRIPT_RUN = 3;

  /**
   * 脚本首次执行前的模块初始化
   */
  public final int SCRIPT_PREPARE = 5;

}
