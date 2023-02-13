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
// 文件创建日期: 18-8-12 上午10:46
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/IConsensusContext.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.chain.Block;

import java.security.PublicKey;


/**
 * 共识表达式的执行体上下文
 */
public interface IConsensusContext {

  /**
   * 在 id 指定的见证者上对数据块执行共识.
   */
  boolean doAction(String witnessId, PublicKey key, Block b);

}
