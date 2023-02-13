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
// 文件创建日期: 17-12-14 上午10:43
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/fix/IStateOperator.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.fix;

/**
 * 状态机读取字符后, 返回的操作码, 操作码指示解析器如何移动缓冲区指针.
 */
public interface IStateOperator {

  /** 返回初始状态, 并把之前的字符序列写入输出. */
  int RESET = 4;

  /** 进入状态机, 一旦发生 RESET 将重制到 BEGIN 发生的点.
      该状态结束后, 会以下一个字符再次进入同一个状态机, 必须对此进行处理 */
  int BEGIN = 1;

  /** 完成一次解析, 强制使解析器回到规则的开头 */
  int END = 3;

  /** 保持当前状态机 */
  int KEEP  = 2;

  /** 没有特别的动作 */
  int NOTHING = 0;
  int INIT = 0;

  /** 进入下一个状态, 凡是进入下一个状态机的操作码都必须 >1000 */
  int NEXT = 1000;

  /** 进入下一个状态, 并退回一个字符 */
  int NEXT_AND_BACK = 1001;

  /** 进入下一个状态, 并将字符退回到状态机的起始位置 */
  int NEXT_AND_BACK_ALL = 1002;

}
