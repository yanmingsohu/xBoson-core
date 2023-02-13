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
// 文件创建日期: 18-5-21 下午4:01
// 原始文件路径: E:/xboson/xBoson/src/com/xboson/app/lib/ImageImpl.java
// 授权说明版本: 1.1
//
// [ J.yanming - Q.412475540 ]
//
////////////////////////////////////////////////////////////////////////////////

package com.xboson.app.lib;

import com.xboson.script.lib.Buffer;
import com.xboson.script.lib.Bytes;
import com.xboson.util.StringBufferOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageImpl {

  public Picture read(Buffer.JsBuffer buf) throws IOException {
    return new Picture(new ByteArrayInputStream(buf._buffer().array()));
  }


  public Picture read(Bytes bytes) throws IOException {
    return new Picture(new ByteArrayInputStream(bytes.bin()));
  }


  public Picture read(byte[] b) throws IOException {
    return new Picture(new ByteArrayInputStream(b));
  }


  public class Picture {
    private BufferedImage bi;

    private Picture(InputStream i) throws IOException {
      bi = ImageIO.read(i);
    }

    public int height() {
      return bi.getHeight();
    }

    public int width() {
      return bi.getWidth();
    }

    public Buffer.JsBuffer toBuffer(String format) throws IOException {
      StringBufferOutputStream out = new StringBufferOutputStream();
      if (ImageIO.write(bi, format, out)) {
        return new Buffer().from(out.toBytes());
      }
      return null;
    }

    public void resize(int x, int y, int width, int height) {
      bi = bi.getSubimage(x, y, width, height);
    }
  }
}
