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
// 文件创建日期: 18-7-30 下午6:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/chain/witness/WitnessConnect.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.chain.witness;

import com.xboson.been.Witness;
import com.xboson.been.XBosonException;
import com.xboson.chain.Block;
import com.xboson.util.Hex;
import com.xboson.util.Tool;
import okhttp3.*;

import java.io.IOException;


/**
 * 连接到见证者客户端
 */
public class WitnessConnect {

  public static final String DELIVER_METHOD = "deliver";
  public static final String SIGN_METHOD = "sign";
  public static final MediaType BINARY
          = MediaType.parse("application/octet-stream");
  public static final int DELIVER_RETRY_COUNT = 3;


  private static OkHttpClient hc;

  private final String host;
  private final int port;
  private final String prefix;


  /**
   * 配置一个到客户端的连接, 实际的物理连接在调用方法后才建立
   * @param host 主机地址
   * @param port 端口
   * @param prefix url 前缀, 可以空
   */
  public WitnessConnect(String host, int port, String prefix) {
    this.host   = host;
    this.port   = port;
    this.prefix = prefix;
  }


  public WitnessConnect(Witness w) {
    this.host   = w.host;
    this.port   = w.port;
    this.prefix = w.urlPerfix;
  }


  /**
   * 执行远程 sign 方法, 任何失败都将抛出异常
   * @return 返回签名后生成的数据
   */
  public byte[] doSign(byte[] data) throws IOException {
    RequestBody body = RequestBody.create(BINARY, data);
    return doSign(body);
  }


  /**
   * @see com.xboson.util.StreamRequestBody
   */
  public byte[] doSign(RequestBody body) throws IOException {
    HttpUrl urlobj = makeUrl(SIGN_METHOD);
    Request.Builder build = new Request.Builder();
    build.url(urlobj);
    build.post(body);

    try (Response resp = openClient().newCall(build.build()).execute()) {
      if (resp.code() == 500) {
        throw new XBosonException(resp.body().string());
      }

      if (resp.code() != 200) {
        throw new XBosonException(resp.message());
      }
      return resp.body().bytes();
    }
  }


  public boolean doDeliver(Block b, String chain, String channel) {
    String json = Tool.getAdapter(Block.class).toJson(b);
    return doDeliver(json, chain, channel);
  }


  /**
   * 将区块数据以 json 格式发送给见证者,
   * 如果见证者没有实现该接口返回 false, 成功返回 true.
   */
  public boolean doDeliver(String json, String chain, String channel) {
    HttpUrl urlobj = makeUrl(DELIVER_METHOD);
    Request.Builder build = new Request.Builder();
    build.url(urlobj);
    build.addHeader("chain",   chain);
    build.addHeader("channel", channel);
    RequestBody body = RequestBody.create(BINARY, json);
    build.post(body);

    for (int retry=0; retry < DELIVER_RETRY_COUNT; ++retry) {
      try (Response resp = openClient().newCall(build.build()).execute()) {
        if (resp.code() == 200) {
          return true;
        }

        if (resp.code() == 404) {
          return false;
        }
      } catch (Exception e) {
        WitnessFactory.me().getWitnessLog().warn(
                "Deliver Block:", json, e);
      }
      Tool.sleep(3000);
    }
    return false;
  }


  private HttpUrl makeUrl(String methodName) {
    HttpUrl.Builder url_build = new HttpUrl.Builder();
    url_build.scheme("http");
    url_build.host(host);
    url_build.port(port);

    if (prefix == null) {
      url_build.addPathSegment(methodName);
    } else {
      url_build.addPathSegments(prefix + methodName);
    }

    return url_build.build();
  }


  private static OkHttpClient openClient() {
    if (hc == null) {
      synchronized (WitnessConnect.class) {
        if (hc == null) {
          //
          // 这个对象可能很昂贵
          //
          hc = new OkHttpClient();
        }
      }
    }
    return hc;
  }
}
