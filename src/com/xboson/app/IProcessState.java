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
// 文件创建日期: 18-1-31 下午12:03
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/IProcessState.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app;

public interface IProcessState {

  /** kill 操作成功 */
  int KILL_OK = 0;

  /** kill 目标不存在 */
  int KILL_NO_EXIST = 1;

  /** 初始化未完成, 不能 kill */
  int KILL_NO_READY = 2;

  /** 已经被 kill */
  int KILL_IS_KILLED = 3;

}
