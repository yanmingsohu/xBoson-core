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
// 文件创建日期: 18-3-17 上午10:53
// 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/j2ee/emu/BufScrvletOutputStream.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.j2ee.emu;

import com.xboson.util.StringBufferOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;


public class BufScrvletOutputStream extends ServletOutputStream {

  public final StringBufferOutputStream out;


  public BufScrvletOutputStream() {
    out = new StringBufferOutputStream(1000);
  }


  @Override
  public boolean isReady() {
    return true;
  }


  @Override
  public void setWriteListener(WriteListener writeListener) {
  }


  @Override
  public void write(int i) throws IOException {
    out.write(i);
  }
}
