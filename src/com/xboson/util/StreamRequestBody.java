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
// 文件创建日期: 18-8-13 下午4:58
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/util/StreamRequestBody.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.util;

import com.xboson.been.XBosonException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;


/**
 * 为 okhttp3 的 Body 提供流式操作支持.
 * 操作完成后输入流将被关闭.
 */
public class StreamRequestBody extends RequestBody {

  private InputStream i;
  private MediaType mt;


  public StreamRequestBody(InputStream i, MediaType mt) {
    if (i == null)
      throw new XBosonException.NullParamException("InputStream i");

    this.i  = i;
    this.mt = mt;
  }


  public StreamRequestBody(InputStream i, String mediaType) {
    this(i, MediaType.parse(mediaType));
  }


  public StreamRequestBody(InputStream i) {
    this(i, (MediaType)null);
  }


  @Nullable
  @Override
  public MediaType contentType() {
    return mt;
  }


  @Override
  public void writeTo(BufferedSink sink) throws IOException {
    Tool.copy(i, sink.outputStream(), true);
  }
}
