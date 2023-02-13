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
// 文件创建日期: 18-12-16 下午12:16
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/app/lib/DockerImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.been.XBosonException;
import com.xboson.util.CreatorFromUrl;
import com.xboson.util.SSL;
import com.xboson.util.Tool;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.cert.Certificate;
import java.util.Map;


public class DockerImpl extends RuntimeUnitImpl {

  public final String DEFAULT_VERSION = "1.18";

  private CreatorFromUrl<IDockerClient> clientCreator;


  public DockerImpl() {
    super(null);
    clientCreator = new CreatorFromUrl<>();

    clientCreator.reg("http", (v, p, uris, data)->
            new Http(uris, null));

    clientCreator.reg("https", (v, p, uris, data)->
            new Http(uris, data));

    clientCreator.reg("unix", (v, p, uris, data)-> {
      throw new UnsupportedOperationException();
    });
  }


  public IDockerClient open(String uri) {
    return open(uri, null);
  }


  /**
   * 打开到服务器链接, tslConfig 保存 TSL 配置参数
   * tslConfig.cert 客户端证书, X509-PEM 格式
   * tslConfig.key  证书私钥, pkcs8 格式
   * tslConfig.ca   CA 证书, X509-PEM 格式
   * tslConfig.pass 加密证书和私钥的密钥
   */
  public IDockerClient open(String uri, Object tslConfig) {
    return clientCreator.create(uri, tslConfig);
  }


  public interface IDockerClient {

    /**
     * 设置 docker api 版本
     */
    void setVersion(String v);


    /**
     * 调用接口返回数据
     */
    Object call(String api, Map<String, Object> data) throws Exception;


    default Object call(String api) throws Exception {
      return call(api, null);
    }
  }


  /**
   * TODO: 该对象必须缓存
   */
  private class Http implements IDockerClient {

    private String ver = DEFAULT_VERSION;
    private String host;
    private OkHttpClient hc;


    private Http(String url, Object data) throws Exception {
      this.host = Tool.urlNoSuffix(url);
      OkHttpClient.Builder cb = new OkHttpClient.Builder();

      if (url.startsWith("https://")) {
        if (data == null) {
          throw new XBosonException("SSL Certificate needed");
        }
        bindSslParamter(cb, data);
      }
      this.hc = cb.build();
    }


    private void bindSslParamter(OkHttpClient.Builder cb, Object data)
            throws Exception {
      Map<String, Object> config = (Map<String, Object>) data;
      String cert = (String) config.get("cert");
      String key  = (String) config.get("key");
      String ca   = (String) config.get("ca");
      String pass  = (String) config.get("pass");

      SSL ssl = new SSL(pass);
      ssl.addCertificate(ssl.toStream(ca));
      Certificate c = ssl.addCertificate(ssl.toStream(cert));
      ssl.addPrivateKey(c, key);

      cb.sslSocketFactory(ssl.getSocketFactory(), ssl.getX509TrustManager());
      cb.hostnameVerifier(SSL.APASS);
    }


    @Override
    public void setVersion(String v) {
      this.ver = v;
    }


    @Override
    public Object call(String api, Map<String, Object> data) throws Exception {
      String url = host +"/v"+ ver +"/"+ api;
      HttpUrl.Builder url_build = HttpUrl.parse(url).newBuilder();

      if (data != null) {
        for (Map.Entry<String, Object> en : data.entrySet()) {
          Object o = en.getValue();
          url_build.addQueryParameter(en.getKey(), String.valueOf(o));
        }
      }

      Request.Builder build = new Request.Builder();
      build.url(url_build.build());

      try (Response resp = hc.newCall(build.build()).execute()) {
        return jsonParse(resp.body().string());
      }
    }
  }

}
