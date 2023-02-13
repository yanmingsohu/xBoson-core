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
// 文件创建日期: 20-10-30 上午8:24
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/BouncyCastleProviderReg.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.log.Log;
import com.xboson.log.LogFactory;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;


public class BouncyCastleProviderReg {

  static {
    BouncyCastleProvider prv = new BouncyCastleProvider();
    prv.addAlgorithm("Cipher.SM2C132", BouncyCastleProviderReg.SM2c132.class.getName());
    Security.addProvider(prv);
  }


  public static void me() {
    Log log = LogFactory.create("bouncyCastleProviderRegister");
    log.info("Bouncy Castle(JCE) Provider Registed");
  }


  public static class SM2c132 extends GMCipherSpi {

    public SM2c132() {
      super(new SM2Engine(SM2Engine.Mode.C1C3C2));
    }
  }
}
