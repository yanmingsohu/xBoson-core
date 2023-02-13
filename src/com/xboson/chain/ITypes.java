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
// 文件创建日期: 18-7-13 下午12:41
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/ITypes.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

/**
 * 代码必须是连续的
 */
public interface ITypes {

  int FAIL              = 0;
  int GENESIS           = 1;
  int NORM_DATA         = 2;
  int ENCRYPTION_DATA   = 3;
  int CHAINCODE_CONTENT = 4;
  int MESSAGE           = 5;

  /** 可能的最大值, 如果有新的枚举加入需要更新 */
  int LENGTH = 5;
}
