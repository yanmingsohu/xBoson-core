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
// 文件创建日期: 18-2-2 上午9:38
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/ECDSA.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


/**
 * ECDSA 的对称加密算法辅助类, 使用 PEM 格式.
 *
 * 该对象依赖 BouncyCastleProvider 类,
 * BouncyCastleProvider 在 bcprov-jdk15on-1.66.jar 包,
 * <del>该包已经集成在 fabric-sdk-java 中.</del>
 */
public class ECDSA {

  private static ECDSA instance;
  private KeyFactory fact;
  private BouncyCastleProvider provider;


  private ECDSA() {
    try {
      provider = new BouncyCastleProvider();
      fact = KeyFactory.getInstance("ECDSA", provider);
    } catch (NoSuchAlgorithmException e) {
      Tool.pl("WARN", e);
    }
  }


  public static ECDSA me() {
    if (instance == null) {
      synchronized (ECDSA.class) {
        if (instance == null) {
          instance = new ECDSA();
        }
      }
    }
    return instance;
  }


  /**
   * 将 pem 格式的私钥解析为私钥对象
   */
  public PrivateKey parsePrivateKey(String pem) {
    try {
      if (fact == null)
        fact = KeyFactory.getInstance("ECDSA", provider);

      String key = formatPrivateKey(pem);
      byte[] encoded = DatatypeConverter.parseBase64Binary(key);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
      return fact.generatePrivate(keySpec);

    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new XBosonException(e);
    } catch (IOException e) {
      throw new XBosonException.IOError(e);
    }
  }


  public String formatPrivateKey(String pem) throws IOException {
    BufferedReader br = new BufferedReader(new StringReader(pem));
    StringBuilder builder = new StringBuilder();
    boolean begin = false;
    boolean end = false;

    for (String line = br.readLine(); line != null; line = br.readLine()) {
      if (line.startsWith("-----BEGIN ")
              && line.endsWith(" PRIVATE KEY-----")) {
        begin = true;
        break;
      }
    }

    if (begin) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (line.startsWith("-----END ")
                && line.endsWith(" PRIVATE KEY-----")) {
          end = true;
          break;
        }
        builder.append(line);
      }
    } else {
      throw new IOException("Bad PRIVATE KEY Format, Can not find the head");
    }
    if (!end) {
      throw new IOException("Bad PRIVATE KEY Format, Can not find the tail");
    }
    return builder.toString();
  }
}
