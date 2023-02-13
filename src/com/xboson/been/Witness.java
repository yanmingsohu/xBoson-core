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
// 文件创建日期: 18-8-13 下午2:51
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/been/Witness.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.been;

import com.xboson.chain.Btc;
import com.xboson.util.Hex;

import java.security.PublicKey;


public class Witness {

  public final String id;
  public final String publicKeyStr;
  public final String urlPerfix;
  public final String host;
  public final String algorithm;
  public final int port;


  public Witness(String id, String publicKeyStr, String prefix, String host,
                 String algorithm, int port) {
    this.id           = id;
    this.publicKeyStr = publicKeyStr;
    this.urlPerfix    = prefix;
    this.host         = host;
    this.algorithm    = algorithm;
    this.port         = port;
  }


  public PublicKey getPublicKey() {
    return Btc.publicKey(Hex.Names.BASE64, publicKeyStr);
  }
}
