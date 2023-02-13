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
// 文件创建日期: 18-8-15 下午5:07
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/converter/BlockJsonConverter.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util.converter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.xboson.chain.Block;
import com.xboson.chain.SignNode;
import com.xboson.util.ConverterInitialization;
import com.xboson.util.Hex;

import javax.annotation.Nullable;
import java.io.IOException;


public class BlockJsonConverter extends JsonAdapter<Block> {

  @Nullable
  @Override
  public Block fromJson(JsonReader r) throws IOException {
    throw new UnsupportedOperationException();
  }


  @Override
  public void toJson(JsonWriter w, @Nullable Block b)
          throws IOException {
    w.beginObject();
    w.name("key")
            .value(Hex.encode64(b.key));
    w.name("hash")
            .value(Hex.encode64(b.hash));
    // 创世区块为空
    w.name("previousHash")
            .value(bin(b.previousHash));
    w.name("previousKey")
            .value(bin(b.previousKey));
    w.name("create")
            .value(b.create.getTime());

    w.name("data")
            .value(bin(b.getData()));
    w.name("userid")
            .value(b.getUserId());
    // 链码块为空
    w.name("chaincodeKey")
            .value(bin(b.getChaincodeKey()));
    w.name("apiPath")
            .value(b.getApiPath());
    w.name("apiHash")
            .value(b.getApiHash());
    w.name("type")
            .value(b.type);

    sign(w, b.sign);
    w.endObject();
  }


  private void sign(JsonWriter w, SignNode n) throws IOException {
    w.name("sign");
    w.beginArray();
    while (n != null) {
      w.beginObject();
      w.name("id").value(n.id);
      w.name("si").value(Hex.encode64(n.sign));
      w.endObject();
      n = n.next;
    }
    w.endArray();
  }


  /**
   * 允许参数为空, 并返回空.
   */
  private String bin(byte[] b) {
    if (b == null) return null;
    return Hex.encode64(b);
  }


  public void registerAdapter(Moshi.Builder builder) {
    builder.add(Block.class, this);
  }


  public void registerAdapter(ConverterInitialization.JsonFactory f) {
    f.add(Block.class, this);
  }
}
