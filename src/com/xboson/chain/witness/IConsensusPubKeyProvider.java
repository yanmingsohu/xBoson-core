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
// 文件创建日期: 18-8-13 上午11:33
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/IConsensusPubKeyProvider.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.been.XBosonException;

import java.security.PublicKey;


public interface IConsensusPubKeyProvider {

  /**
   * 返回见证者的公钥, 公钥不存在将抛出 NotExist,
   * 实现必须保证返回一个有效的公钥.
   * 该方法在构造共识对象时被调用, 之后将被持久化在区块链上.
   */
  PublicKey getKey(String witness_id) throws XBosonException.NotExist;

}
