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
// 文件创建日期: 18-12-17 上午9:34
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/test/TestSSL.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.test;

import com.xboson.util.SSL;
import com.xboson.util.Tool;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.cert.Certificate;


public class TestSSL {

  final static String cert = "-----BEGIN CERTIFICATE-----\n" +
          "MIIFAzCCAuugAwIBAgIUAIxqv3Iwft0I1Kw/m6U4e20iJhwwDQYJKoZIhvcNAQEL\n" +
          "-----END CERTIFICATE-----";

  final static String ca = "-----BEGIN CERTIFICATE-----\n" +
          "MIIFezCCA2OgAwIBAgIUOeYE2nsgdieER+9oA3eWDtnngdkwDQYJKoZIhvcNAQEL\n" +
          "-----END CERTIFICATE-----";

  final static String key = "-----BEGIN PRIVATE KEY-----\n" +
          "MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQDENVoy/OF593iL\n" +
          "-----END PRIVATE KEY-----";

  final static String pass = "0000";
  final static String url  = "https://10.0.0.3:2375/v1.18/info";

  /**
   * 该测试不在应用上下文, 也不在标准测试用例中.
   * 测试前按照 https://docs.docker.com/engine/security/https/ 的说明配置服务器
   * 将生成的证书和使用的密钥配置到全局变量中.
   */
  public static void main(String []av) throws Exception {
    SSL ssl = new SSL("TLS", pass);
    Certificate c;
    // THROW: Software caused connection abort: recv failed
    c = ssl.addCertificate(ssl.toStream(cert));
    // THROW: unable to find valid certification path to requested target
    ssl.addCertificate(ssl.toStream(ca));
    ssl.addPrivateKey(c, key);

    OkHttpClient.Builder cb = new OkHttpClient.Builder();
    cb.sslSocketFactory(ssl.getSocketFactory(), ssl.getX509TrustManager());
    cb.hostnameVerifier(SSL.APASS);
    OkHttpClient hc = cb.build();

    HttpUrl.Builder url_build = HttpUrl.parse(url).newBuilder();
    Request.Builder build = new Request.Builder();
    build.url(url_build.build());
    build.get();

    Response resp = hc.newCall(build.build()).execute();
    Tool.pl("Docker 回应:", resp.body().string());
  }
}
