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
// 文件创建日期: 18-8-12 上午10:47
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/ParmUnit.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.chain.Block;

import java.security.PublicKey;


public class ParmUnit implements IConsensusUnit {

  private String id;
  private PublicKey key;


  public ParmUnit(String id, PublicKey pubkey) {
    this.id  = id;
    this.key = pubkey;
  }


  @Override
  public void addAction(IConsensusUnit subAct) {
    throw new UnsupportedOperationException();
  }


  @Override
  public boolean doAction(IConsensusContext d, Block b) {
    return d.doAction(id, key, b);
  }

}
