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
// 文件创建日期: 18-8-13 下午1:52
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/ChainEvent.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import java.io.Serializable;
import java.security.KeyPair;


public class ChainEvent implements Serializable {

  public String chain;
  public String channel;
  public String exp;
  public KeyPair[] kp;


  public ChainEvent(String chain, String channel, String exp, KeyPair[] kp) {
    this.chain    = chain;
    this.channel  = channel;
    this.exp      = exp;
    this.kp       = kp;
  }
}
