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
// 文件创建日期: 18-7-17 上午11:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/ISignerProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain;

import java.security.KeyPair;


public interface ISignerProvider {

  /**
   * 返回通道的签名器, 该方法会创建一个全新的签名器
   * @see com.xboson.chain.witness.ConsensusParser 共识表达式解析
   * @param chainName 链名
   * @param channelName 通道名
   * @param consensusExp 共识表达式, 可以空
   * @param kp 系统密钥对数组
   */
  ISigner getSigner(String chainName, String channelName,
                    String consensusExp, KeyPair[] kp);

}
